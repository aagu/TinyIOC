package com.aagu.ioc.factory

import com.aagu.ioc.bean.BeanDefinition
import com.aagu.ioc.bean.BeanReference
import com.aagu.ioc.bean.GeneralBeanDefinition
import com.aagu.ioc.bean.PropertyValue
import com.aagu.ioc.util.StringUtils
import com.aagu.ioc.util.XmlScanner
import org.dom4j.Element


class XmlBeanFactory(private val xmlFile: String): DefaultBeanFactory() {
    override fun init() {
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
                if (scope == BeanDefinition.SCOPE_PROTOTYPE) {
                    beanDef.setScope(BeanDefinition.SCOPE_PROTOTYPE)
                } else {
                    beanDef.setScope(BeanDefinition.SCOPE_SINGLETON)
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
                                    "list" -> {
                                        val listItems = child.elements("item")
                                        val list = createList(value)
                                        if (value == "beans") {
                                            for (item in listItems) {
                                                insertBeanList(list as ArrayList<Any?>, item)
                                            }
                                        } else {
                                            for (item in listItems) {
                                                insertList(list as ArrayList<Any?>, item, value)
                                            }
                                        }
                                        propertyList.add(PropertyValue(name, list))
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
                                    "list" -> {
                                        val list = createList(textValue)
                                        val listItems = paramNode.elements("item")
                                        if (textValue == "beans") {
                                            for (item in listItems) {
                                                insertBeanList(list as ArrayList<Any?>, item)
                                            }
                                        } else {
                                            for (item in listItems) {
                                                insertList(list as ArrayList<Any?>, item, textValue)
                                            }
                                        }
                                        realValue = list
                                    }
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

    private fun insertBeanList(list: ArrayList<Any?>, item: Element) {
        val value = item.attributeValue("value")
        list.add(getBean(value))
    }

    private fun insertList(list: ArrayList<Any?>, item: Element, value: String?) {
        when (value) {
            "string" -> list.add(item.attributeValue("value"))
            "int" -> list.add(item.attributeValue("value").toInt())
            else -> list.add(item.attributeValue("value") as Any?)
        }
    }

    private fun createList(value: String?): List<Any?> {
        return when (value) {
            "string" -> ArrayList<String>()
            "int" -> ArrayList<Int>()
            else -> ArrayList()
        }
    }
}