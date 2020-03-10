package com.aagu.ioc.factory

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Config
import com.aagu.ioc.exception.BeanNotFoundException
import com.aagu.ioc.util.BeanUtils
import com.aagu.ioc.util.PackageScanner
import com.aagu.ioc.util.StringUtils
import java.util.Collections.synchronizedList

class AnnotationBeanFactory(private val packageNames: List<String>): DefaultBeanFactory() {
    private val interfaceList = synchronizedList(ArrayList<Class<*>>())
    private val scanFilters = synchronizedList(ArrayList<PackageScanner.Filter>())
    private val scanner = PackageScanner()

    override fun init() {

        val beanAnnotationFilter = object :PackageScanner.Filter {
            override fun onFilter(clazz: Class<*>) {
                if (clazz.isAnnotationPresent(Bean::class.java)) {
                    BeanUtils.registerBeanDefinition(clazz,  this@AnnotationBeanFactory)
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
        val interfaceAFilter = object :PackageScanner.Filter {
            override fun onFilter(clazz: Class<*>) {
                if (clazz.isInterface) {
                    interfaceList.add(clazz)
                }
            }
        }
        scanner.addFilter(beanAnnotationFilter)
        scanner.addFilter(configAnnotationFilter)
        scanner.addFilter(interfaceAFilter)
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
        for (clazz in interfaceList) {
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
        interfaceList.clear()
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
            val v = def.value.getBeanClass()
            if (v != null) {
                if (clazz.isAssignableFrom(v)) result.add(k)
            }
        }
        return result
    }
}