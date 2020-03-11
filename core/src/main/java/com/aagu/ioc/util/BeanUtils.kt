package com.aagu.ioc.util

import com.aagu.ioc.annotation.*
import com.aagu.ioc.bean.*
import com.aagu.ioc.exception.IllegalBeanDefinitionException
import com.aagu.ioc.exception.PropertyNotFoundException
import java.lang.reflect.Method

object BeanUtils {
    fun registerBeanDefinition(clazz: Class<*>, registry: BeanDefinitionRegistry) {
        val atBean = clazz.getAnnotation(Bean::class.java)
        val beanName = processBeanNameByClass(atBean, clazz)
        val beanDef = GeneralBeanDefinition()
        beanDef.setBeanClass(clazz)
        processScope(atBean, beanDef)
        processInitAndDestroy(clazz, beanDef)
        processProperties(clazz, beanDef)
        takeCareConstructor(clazz, beanName)
        registry.registerBeanDefinition(beanName, beanDef)
    }

    fun registerConfig(clazz: Class<*>, registry: BeanDefinitionRegistry) {
        // 首先把Config注册成bean
        val configName = clazz.simpleName
        val configBeanDef = GeneralBeanDefinition()
        configBeanDef.setBeanClass(clazz)
        registry.registerBeanDefinition(configName, configBeanDef)

        val beanDeclareMethod = clazz.methods.filter { method -> method.isAnnotationPresent(Bean::class.java) }
        for (method in beanDeclareMethod) {
            val atBean = method.getAnnotation(Bean::class.java)
            val beanName = processBeanNameByMethod(atBean, method)
            val beanClazz = method.returnType
            val beanDef = GeneralBeanDefinition()
            processScope(atBean, beanDef)
            beanDef.setFactoryBeanName(configName)
            beanDef.setFactoryMethodName(method.name)
            processInitAndDestroy(beanClazz, beanDef)
            processProperties(beanClazz, beanDef)
            registry.registerBeanDefinition(beanName, beanDef)
        }
    }

    private fun processBeanNameByClass(atBean: Bean, clazz: Class<*>): String {
        var beanName = atBean.beanName
        if (StringUtils.isEmpty(beanName)) {
            beanName = clazz.simpleName
        }
        return StringUtils.lowerCaseFirstChar(beanName)
    }

    private fun processScope(atBean: Bean, beanDef: GeneralBeanDefinition) {
        val scope = atBean.scope
        if (scope == BeanDefinition.SCOPE_PROTOTYPE) {
            beanDef.setScope(BeanDefinition.SCOPE_PROTOTYPE)
        } else {
            beanDef.setScope(BeanDefinition.SCOPE_SINGLETON)
        }
    }

    private fun processInitAndDestroy(clazz: Class<*>, beanDef: GeneralBeanDefinition) {
        val methods = clazz.methods
        for (method in methods) {
            if (method.isAnnotationPresent(InitMethod::class.java)) {
                beanDef.setInitMethodName(method.name)
            }
            if (method.isAnnotationPresent(DestroyMethod::class.java)) {
                beanDef.setDestroyMethodName(method.name)
            }
        }
    }

    private fun processProperties(clazz: Class<*>, beanDef: GeneralBeanDefinition) {
        val fields = clazz.declaredFields
        val propertyList = ArrayList<PropertyValue>()
        for (field in fields) {
            if (field.isAnnotationPresent(Wire::class.java)) {
                val targetClass = field.type
//                if (targetClass.`package`.name != packageName) continue
                propertyList.add(PropertyValue(field.name, BeanReference(StringUtils.lowerCaseFirstChar(targetClass.simpleName))))
            }
            if (field.isAnnotationPresent(Value::class.java)) {
                val anno = field.getAnnotation(Value::class.java)
                var value:String = anno.value
                if (value.isNotBlank()) {
                    if (value.startsWith("#")) {
                        try {
                            value = handlePropValue(value)
                        } catch (e: PropertyNotFoundException) {
                            continue
                        }
                    }
                    when (field.type) {
                        Int::class.java -> {
                            propertyList.add(PropertyValue(field.name, value.toInt()))
                        }
                        Long::class.java -> {
                            propertyList.add(PropertyValue(field.name, value.toLong()))
                        }
                        Float::class.java -> {
                            propertyList.add(PropertyValue(field.name, value.toFloat()))
                        }
                        else -> {
                            propertyList.add(PropertyValue(field.name, value))
                        }
                    }
                }
            }
        }
        if (propertyList.isNotEmpty()) beanDef.setPropertyValues(propertyList)
    }

    private fun takeCareConstructor(clazz: Class<*>, beanName: String) {
        val constructors = clazz.constructors
        var findConstructorWithEmptyPropertyValue = false
        for (c in constructors) {
            if (c.parameterCount == 0) {
                findConstructorWithEmptyPropertyValue = true
                break
            }
        }
        if (!findConstructorWithEmptyPropertyValue)
            throw IllegalBeanDefinitionException("名为 $beanName 的Bean没有无参构造函数！ 无法构造")
    }

    @Throws(PropertyNotFoundException::class)
    private fun handlePropValue(value: String): String {
        val propGroups = StringUtils.getGroupsFromRegex(value, "\\#\\{(\\w+)\\}(:(\\w+))?")
        if (propGroups.isEmpty()) {
            throw PropertyNotFoundException("找不到prop: $value 的定义")
        }
        val propValue = propGroups[0]
        if (propValue != null) {
            return PropertyLoader.getProperty(propValue)
                ?: if (propGroups.size > 2) {
                    propGroups[2]!!
                } else throw PropertyNotFoundException("找不到prop: $propValue 的定义")
        }
        return value
    }

    private fun processBeanNameByMethod(atBean: Bean, method: Method): String {
        var beanName = atBean.beanName
        if (StringUtils.isEmpty(beanName)) {
            beanName = method.name
        }
        return StringUtils.lowerCaseFirstChar(beanName)
    }
}