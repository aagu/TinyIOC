package com.aagu.ioc.factory

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Config
import com.aagu.ioc.bean.BeanPostProcessor
import com.aagu.ioc.bean.BeanReference
import com.aagu.ioc.bean.FactoryBeanDefinition
import com.aagu.ioc.bean.WireableBeanDefinition
import com.aagu.ioc.exception.BeanNotFoundException
import com.aagu.ioc.util.BeanUtils
import com.aagu.ioc.util.PackageScanner
import com.aagu.ioc.util.StringUtils
import java.util.Collections.synchronizedList
import java.util.Collections.synchronizedSet

class AnnotationBeanFactory(private val packageNames: List<String>): AbstractBeanFactory() {
    private val interfaceSet = synchronizedSet(HashSet<Class<*>>())
    private val scanFilters = synchronizedList(ArrayList<PackageScanner.Filter>())
    private val scanner = PackageScanner()

    override fun init() {

        val beanAnnotationFilter = object :PackageScanner.Filter {
            override fun onFilter(clazz: Class<*>) {
                if (clazz.isAnnotationPresent(Bean::class.java)) {
                    BeanUtils.registerBeanDefinition(clazz, this@AnnotationBeanFactory)
                }
            }
        }
        val configAnnotationFilter = object :PackageScanner.Filter {
            override fun onFilter(clazz: Class<*>) {
                if (clazz.isAnnotationPresent(Config::class.java)) {
                    BeanUtils.registerConfig(clazz, this@AnnotationBeanFactory)
                }
            }
        }
        val interfaceFilter = object :PackageScanner.Filter {
            override fun onFilter(clazz: Class<*>) {
                if (clazz.isInterface && !clazz.isAnnotation) {
                    interfaceSet.add(clazz)
                }
            }
        }
        registerBeanPostProcessor(object : BeanPostProcessor {
            override fun getPriority(): Int {
                return 10
            }

            override fun postProcessAfterInitialization(beanName: String, bean: Any): Any {
                val definition = getBeanDefinition(beanName)
                if (definition is WireableBeanDefinition) {
                    if (definition.getWireProperties().isNotEmpty()) {
                        val clazz = bean.javaClass
                        for (pv in definition.getWireProperties()) {
                            val field = clazz.getDeclaredField(pv.name)
                            field.isAccessible = true
                            field.set(bean, doGetBean((pv.value as BeanReference).getBeanName()))
                        }
                    }
                }
                return bean
            }
        })
        scanner.addFilter(beanAnnotationFilter)
        scanner.addFilter(configAnnotationFilter)
        scanner.addFilter(interfaceFilter)
        scanner.addFilters(scanFilters)
        for (packageName in packageNames) {
            scanner.addPackage(packageName)
        }
        scanner.scan()
        scanner.clearFilters()
        scanner.clearPackage()
    }

    override fun finalizeInit() {
        super.finalizeInit()
        for (clazz in interfaceSet) {
            val impl = searchImplCandidates(clazz)
            if (impl.isNotEmpty()) {
                try {
                    val implBeanDefinition = getBeanDefinition(impl[0])
                    val beanName = StringUtils.lowerCaseFirstChar(clazz.simpleName)
                    registerBeanDefinition(beanName, implBeanDefinition)
                } catch (e: BeanNotFoundException) {

                }
            }
        }
        interfaceSet.clear()
    }

    fun addOnScanFilter(filter: PackageScanner.Filter) {
        scanFilters.add(filter)
    }

    fun clearScanFilters() {
        scanFilters.clear()
    }

    private fun searchImplCandidates(clazz: Class<*>): ArrayList<String> {
        val beanDefs = beanDefinitionMap.entries
        val result = ArrayList<String>()
        for (def in beanDefs) {
            val k = def.key
            if (StringUtils.lowerCaseFirstChar(clazz.simpleName) == k) {
                return arrayListOf(k)
            }
            val v = def.value.getBeanClass()
            if (v != null) {
                if (BeanUtils.isAssignableFrom(clazz, v)) {
                    result.add(k)
                }
            } else {
                //try factory construction
                var factoryMethod = def.value.getFactoryMethod()
                if (factoryMethod == null) {
                    val factoryMethodName = def.value.getFactoryMethodName()
                    factoryMethodName?.let {
                        //if factory method name is not empty, then we can get factory bean name
                        val factoryBeanClass = getBean<Any>(def.value.getFactoryBeanName()!!)::class.java
                        val factoryConstructorArgs: Array<*>? = def.value.getConstructorArguments()
                        factoryMethod = if (factoryConstructorArgs == null) {
                            factoryBeanClass.getMethod(def.value.getFactoryMethodName()!!)
                        } else {
                            val typedArgs = factoryConstructorArgs.map {
                                    value -> if (value == null) null else value::class.java
                            }.toTypedArray()
                            factoryBeanClass.getMethod(def.value.getFactoryMethodName()!!, *typedArgs)
                        }
                        factoryMethod?.let {
                            // cache it
                            def.value.setFactoryMethod(it)
                        }
                    }
                }

                if (factoryMethod != null) {
                    val returnType = factoryMethod!!.returnType
                    if (clazz.isAssignableFrom(returnType)) {
                        result.add(k)
                    } else {
                        val definition = def.value
                        if (definition is FactoryBeanDefinition && definition.allowGenericType) {
                            result.add(k)
                        }
                    }
                }
            }
        }
        return result
    }
}