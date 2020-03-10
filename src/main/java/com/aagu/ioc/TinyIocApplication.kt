package com.aagu.ioc

import com.aagu.aop.advisor.AdvisorManager
import com.aagu.aop.proxy.AdvisorAutoProxyCreator
import com.aagu.ioc.annotation.Application
import com.aagu.ioc.context.support.PropertiesApplicationContext
import com.aagu.ioc.factory.BeanFactory
import com.aagu.ioc.factory.XmlBeanFactory
import com.aagu.ioc.util.PropertyLoader
import com.aagu.ioc.util.StringUtils
import com.aagu.webServer.Bootstrap
import sun.misc.Signal

const val WEB_SERVER_PACKAGE_NAME = "com.aagu.web"

public inline fun <reified T : TinyIocApplication> runWithAnnotation(clazz: Class<T>, args: Array<String>) {
    val appAnno = clazz.getAnnotation(Application::class.java)
    val packageName = if (StringUtils.isNotEmpty(appAnno.basePackage)) appAnno.basePackage else clazz.`package`.name
    val propDef = appAnno.property
    var enableAop = false
    var enableWeb = false
    var advisorManager: AdvisorManager? = null
    if (StringUtils.isNotEmpty(propDef)) {
        PropertyLoader.load(propDef)
        enableAop = PropertyLoader.getProperty("enable-aop") == "true"
        enableWeb = PropertyLoader.getProperty("enable-web") == "true"
    }
    val scannedPackages = ArrayList<String>()
    scannedPackages.add(packageName)
    scannedPackages.add(WEB_SERVER_PACKAGE_NAME)
    val ioc = PropertiesApplicationContext(scannedPackages)
    if (enableAop) {
        advisorManager = AdvisorManager()
        ioc.addOnScanFilter(advisorManager)
        ioc.registerFactoryPostProcessor(advisorManager)
    }
    ioc.refresh()
    if (enableAop) {
        val advisorAutoProxyCreator = AdvisorAutoProxyCreator(ioc)
        advisorAutoProxyCreator.setAdvisors(advisorManager!!.getAdvisors())
        ioc.registerBeanPostProcessor(advisorAutoProxyCreator)
    }

    if (enableWeb) {
        val webServer = Bootstrap(PropertyLoader.getConfigs())
        webServer.setApplicationContext(ioc)
        webServer.start()
    } else {
        val instance = clazz.newInstance()
        instance.setBeanContainer(ioc)
        instance.run(args)
    }
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

    init {
        Signal.handle(Signal("INT")) {
            println("signal captured, shutting down...")
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