package com.aagu.test

import com.aagu.ioc.factory.AnnotationBeanFactory
import com.aagu.ioc.factory.XmlBeanFactory
import com.aagu.ioc.util.StringUtils

fun main() {
    val ioc = XmlBeanFactory("beans.xml")
//    val ioc = AnnotationBeanFactory("com.aagu.test")
    ioc.init()
//    val beanDef = GeneralBeanDefinition()
//    beanDef.setBeanClass(Bean1::class.java)
//    beanDef.setConstructorArguments(arrayOf(5))
//    ioc.registerBeanDefinition("bean1", beanDef)
//    val beanDef2 = GeneralBeanDefinition()
//    beanDef2.setBeanClass(Bean2::class.java)
//    beanDef2.setPropertyValues(listOf(PropertyValue("bean1", ioc.getBean("bean1"))))
//    ioc.registerBeanDefinition("bean2", beanDef2)
//    val beanDef3 = GeneralBeanDefinition()
//    beanDef3.setBeanClass(Bean3::class.java)
//    beanDef3.setPropertyValues(listOf(PropertyValue("bean2", ioc.getBean("bean2"))))
//    ioc.registerBeanDefinition("bean3", beanDef3)
    println(ioc.getBean<Bean1>("bean1"))
    println(ioc.getBean(Bean2::class.java))
    println(ioc.getBean(Bean3::class.java))
    ioc.close()
}