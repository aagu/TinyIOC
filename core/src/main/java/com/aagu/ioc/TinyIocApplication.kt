package com.aagu.ioc

import com.aagu.ioc.annotation.Application
import com.aagu.ioc.context.ConfigurationCenter
import com.aagu.ioc.context.support.PropertiesApplicationContext
import com.aagu.ioc.factory.BeanFactory
import com.aagu.ioc.factory.XmlBeanFactory
import com.aagu.ioc.util.PropertyLoader
import com.aagu.ioc.util.StringUtils
import sun.misc.Signal

public inline fun <reified T : TinyIocApplication> runWithAnnotation(clazz: Class<T>, args: Array<String>) {
    val appAnno = clazz.getAnnotation(Application::class.java)
    val packageName = if (StringUtils.isNotEmpty(appAnno.basePackage)) appAnno.basePackage else clazz.`package`.name
    val propDef = appAnno.property
    if (StringUtils.isNotEmpty(propDef)) {
        PropertyLoader.load(propDef)
    }
    val scannedPackages = ArrayList<String>()
    scannedPackages.add(packageName)
    val ioc = PropertiesApplicationContext(scannedPackages)
    val configurationCenter = ConfigurationCenter(ioc)
    configurationCenter.configureBeforeContextRefresh()
    ioc.refresh()
    configurationCenter.configureAfterContextRefresh()

    val instance = clazz.newInstance()
    instance.setBeanContainer(ioc)
    ioc.use {
        instance.run(args)
    }
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
    ioc.use {
        instance.run(args)
    }
}

abstract class TinyIocApplication {
    private lateinit var beanFactory: BeanFactory
    abstract fun run(args: Array<String>)

    init {
        Signal.handle(Signal("INT")) {
            println("signal captured, closing container...")
            beanFactory.close()
        }
    }

    fun <T> getBean(name: String): T {
        return beanFactory.getBean(name)
    }

    fun <T> getBean(clazz: Class<T>): T {
        return beanFactory.getBean(clazz)
    }

    fun setBeanContainer(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }
}