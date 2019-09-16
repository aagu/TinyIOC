package com.aagu.ioc.factory

import com.aagu.ioc.annotation.*
import com.aagu.ioc.bean.BeanDefinition
import com.aagu.ioc.bean.BeanReference
import com.aagu.ioc.bean.GeneralBeanDefinition
import com.aagu.ioc.bean.PropertyValue
import com.aagu.ioc.exception.IllegalBeanDefinitionException
import com.aagu.ioc.util.PackageScanner
import com.aagu.ioc.util.PropertyLoader
import com.aagu.ioc.util.StringUtils

class AnnotationBeanFactory(private val packageName: String): DefaultBeanFactory() {
    fun init() {
        val scanner = PackageScanner()
        scanner.setFilter(object : PackageScanner.Companion.Filter {
            override fun accept(clazz: Class<*>): Boolean {
                return clazz.isAnnotationPresent(Bean::class.java)
            }
        })
        scanner.setListener(object : PackageScanner.Companion.Listener {
            override fun onScanClass(clazz: Class<*>) {
                val atBean = clazz.getAnnotation(Bean::class.java)
                var beanName = atBean.beanName
                if (StringUtils.isEmpty(beanName)) {
                    beanName = clazz.simpleName
                }
                beanName = StringUtils.lowerCaseFirstChar(beanName)
                val scope = atBean.scope
                val beanDef = GeneralBeanDefinition()
                beanDef.setBeanClass(clazz)
                if (scope == BeanDefinition.SCOPE_PROTOTYPE) {
                    beanDef.setScope(BeanDefinition.SCOPE_PROTOTYPE)
                } else {
                    beanDef.setScope(BeanDefinition.SCOPE_SINGLETON)
                }
                val methods = clazz.methods
                for (method in methods) {
                    if (method.isAnnotationPresent(InitMethod::class.java)) {
                        beanDef.setInitMethodName(method.name)
                    }
                    if (method.isAnnotationPresent(DestroyMethod::class.java)) {
                        beanDef.setDestroyMethodName(method.name)
                    }
                }
                val fields = clazz.declaredFields
                val propertyList = ArrayList<PropertyValue>()
                for (field in fields) {
                    if (field.isAnnotationPresent(Wire::class.java)) {
                        val targetClass = field.type
                        if (targetClass.`package`.name != packageName) continue
                        propertyList.add(PropertyValue(field.name, BeanReference(targetClass.simpleName)))
                    }
                    if (field.isAnnotationPresent(Value::class.java)) {
                        val anno = field.getAnnotation(Value::class.java)
                        var value = anno.value
                        if (value.isNotBlank()) {
                            val propValue = StringUtils.getValueFromRegex(value, "\\#\\{(.*)\\}")
                            if (propValue != null) {
                                val pValue = PropertyLoader.getProperty(propValue)
                                if (pValue != null) value = pValue
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
                registerBeanDefinition(beanName, beanDef)
            }
        })
        scanner.addPackage(packageName)
        scanner.scan()
    }
}