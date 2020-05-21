package com.aagu.ioc.context.support

import com.aagu.ioc.bean.BeanDefinition
import com.aagu.ioc.bean.BeanPostProcessor
import com.aagu.ioc.factory.AnnotationBeanFactory
import com.aagu.ioc.factory.BeanFactory
import com.aagu.ioc.factory.FactoryPostProcessor
import com.aagu.ioc.util.PackageScanner

class PropertiesApplicationContext(private val packageNames: ArrayList<String>) : AbstractApplicationContext() {
    private var beanFactory: AnnotationBeanFactory = AnnotationBeanFactory(packageNames)
    private val registeredBeanNames = HashSet<String>()

    override fun refreshBeanFactory() {
        beanFactory.registerBeanPostProcessor(object : BeanPostProcessor {
            override fun postProcessAfterInitialization(beanName: String, bean: Any): Any {
                registeredBeanNames.add(beanName)
                return bean
            }
        })
        loadBeanDefinitions()
    }

    override fun getBeanFactory(): BeanFactory {
        return beanFactory
    }

    override fun <T> getBean(name: String): T {
        return beanFactory.getBean(name) as T
    }

    override fun <T> getBean(clazz: Class<T>): T {
        return beanFactory.getBean(clazz)
    }

    override fun registerBeanPostProcessor(processor: BeanPostProcessor) {
        beanFactory.registerBeanPostProcessor(processor)
    }

    override fun registerFactoryPostProcessor(processor: FactoryPostProcessor) {
        beanFactory.registerFactoryPostProcessor(processor)
    }

    override fun registerBeanDefinition(name: String, definition: BeanDefinition) {
        beanFactory.registerBeanDefinition(name, definition)
    }

    override fun getBeanDefinition(name: String): BeanDefinition {
        return beanFactory.getBeanDefinition(name)
    }

    override fun containsBeanDefinition(name: String): Boolean {
        return beanFactory.containsBeanDefinition(name)
    }

    override fun getRegisteredBeanNames(): List<String> {
        return beanFactory.getRegisteredBeanNames()
    }

    override fun close() {
        beanFactory.close()
    }

    private fun loadBeanDefinitions() {
        beanFactory.init()
        beanFactory.finalizeInit()

        val beanNames = beanFactory.getRegisteredBeanNames()

        for (name in beanNames) {
            getBean<Any>(name)
        }

        beanFactory.clearScanFilters()
    }

    fun addOnScanFilter(filter: PackageScanner.Filter) {
        beanFactory.addOnScanFilter(filter)
    }

    fun addScanPackage(packageName: String) {
        packageNames.add(packageName)
    }
}