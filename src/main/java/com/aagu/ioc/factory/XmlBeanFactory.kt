package com.aagu.ioc.factory

import com.aagu.ioc.bean.BeanReference
import com.aagu.ioc.bean.GeneralBeanDefinition
import com.aagu.ioc.bean.PropertyValue
import com.aagu.ioc.util.StringUtils
import com.aagu.ioc.util.XmlScanner
import org.dom4j.Element


class XmlBeanFactory(private val xmlFile: String): DefaultBeanFactory() {
    fun init() {
        val scanner = XmlScanner()
        scanner.addXmlFile(xmlFile)
        scanner.setFilter(object : XmlScanner.Companion.Filter{
            override fun filterByTag(): String {
                return "bean"
            }

            override fun accept(node: Element): Boolean {
                return node.attributeValue("class").isNotBlank()
            }
        })
        scanner.setListener(object : XmlScanner.Companion.Listener{
            override fun onScanNode(node: Element) {
                var beanName = node.attributeValue("name")
                val clazz = Class.forName(node.attributeValue("class"))
                if (beanName.isBlank()) {
                    beanName = clazz.simpleName
                }
                beanName = StringUtils.lowerCaseFirstChar(beanName)
                val beanDef = GeneralBeanDefinition()
                beanDef.setBeanClass(clazz)
                val scope = node.attributeValue("scope")
                if (scope != null) {
                    beanDef.setScope(scope)
                }
                val childNodes = node.elements()
                val propertyList = ArrayList<PropertyValue>()
                for (i in 0 until childNodes.size) {
                    val child = childNodes[i]
                    when (child.name) {
                        "property" -> {
                            val name = child.attributeValue("name")
                            val ref = child.attributeValue("ref")
                            if (ref != null) {
                                propertyList.add(PropertyValue(name, BeanReference(ref)))
                            } else {
                                val type = child.attributeValue("type")
                                val value = child.attributeValue("value")
                                when (type) {
                                    "int" -> {
                                        propertyList.add(PropertyValue(name, value.toInt()))
                                    }
                                    "long" -> {
                                        propertyList.add(PropertyValue(name, value.toLong()))
                                    }
                                    "float" -> {
                                        propertyList.add(PropertyValue(name, value.toFloat()))
                                    }
                                    else -> {
                                        propertyList.add(PropertyValue(name, value))
                                    }
                                }
                            }
                        }
                        "initMethod" -> {
                            val methodName = child.stringValue
                            if (methodName.isNotBlank()) beanDef.setInitMethodName(methodName)
                        }
                        "destroyMethod" -> {
                            val methodName = child.stringValue
                            if (methodName.isNotBlank()) beanDef.setDestroyMethodName(methodName)
                        }
                        "constructor" -> {
                            val paramNodes = child.elements()
                            val arguments = arrayOfNulls<Any?>(paramNodes.size)
                            for (j in 0 until paramNodes.size) {
                                val paramNode = paramNodes[j] ?: continue
                                val type = paramNode.attributeValue("type")
                                val textValue = paramNode.attributeValue("value")
                                var realValue:Any? = null
                                when (type) {
                                    "string" -> realValue = textValue
                                    "int" -> realValue = textValue.toInt()
                                    "bean" -> realValue = BeanReference(textValue)
                                }
                                arguments[j] = realValue
                            }
                            beanDef.setConstructorArguments(arguments)
                        }
                    }
                }
                if (propertyList.isNotEmpty()) beanDef.setPropertyValues(propertyList)
                registerBeanDefinition(beanName, beanDef)
            }
        })
        scanner.scan()
    }
}