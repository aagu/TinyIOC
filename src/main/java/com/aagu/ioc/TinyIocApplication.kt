package com.aagu.ioc

import com.aagu.aop.advisor.LogAdvisor
import com.aagu.aop.proxy.AdvisorAutoProxyCreator
import com.aagu.ioc.annotation.Application
import com.aagu.ioc.bean.BeanFactory
import com.aagu.ioc.factory.AnnotationBeanFactory
import com.aagu.ioc.factory.XmlBeanFactory
import com.aagu.ioc.util.PropertyLoader
import com.aagu.ioc.util.StringUtils

public inline fun <reified T : TinyIocApplication> runWithAnnotation(clazz: Class<T>, args: Array<String>) {
    val appAnno = clazz.getAnnotation(Application::class.java)
    val packageName = if (StringUtils.isNotEmpty(appAnno.basePackage)) appAnno.basePackage else clazz.`package`.name
    val propDef = appAnno.property
    if (StringUtils.isNotEmpty(propDef)) {
        PropertyLoader.load(propDef)
    }
    val ioc = AnnotationBeanFactory(packageName)
    ioc.init()
    ioc.finalizeInit()
    val advisorAutoProxyCreator = AdvisorAutoProxyCreator(ioc)
    advisorAutoProxyCreator.registerAdvisor(LogAdvisor("aroundMethodAdvice", ""))
    ioc.registerBeanPostProcessor(advisorAutoProxyCreator)
    val instance = clazz.newInstance()
    instance.setBeanContainer(ioc)
    instance.run(args)
    ioc.close()
}

public inline fun <reified T: TinyIocApplication> runWithXml(clazz: Class<T>, args: Array<String>) {
    val appAnno = clazz.getAnnotation(Application::class.java)
    val xmlLocation = appAnno.xmlLocation
    check(!StringUtils.isEmpty(xmlLocation)) { "找不到xml定义文件" }
    val ioc = XmlBeanFactory(xmlLocation)
    ioc.init()
    ioc.finalizeInit()
    val instance = clazz.newInstance()
    instance.setBeanContainer(ioc)
    instance.run(args)
    ioc.close()
}

abstract class TinyIocApplication {
    private lateinit var beanFactory: BeanFactory
    abstract fun run(args: Array<String>)

    fun <T> getBean(name: String): T {
        return beanFactory.getBean<T>(name)
    }

    fun <T> getBean(clazz: Class<T>): T {
        return beanFactory.getBean(clazz)
    }

    fun setBeanContainer(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }
}