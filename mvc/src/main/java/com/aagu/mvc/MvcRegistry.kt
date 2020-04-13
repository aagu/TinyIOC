package com.aagu.mvc

import com.aagu.ioc.bean.BeanDefinition
import com.aagu.ioc.bean.GeneralBeanDefinition
import com.aagu.ioc.context.support.AbstractApplicationContext
import com.aagu.ioc.factory.AbstractBeanFactory
import com.aagu.ioc.factory.FactoryPostProcessor
import com.aagu.ioc.util.PackageScanner
import com.aagu.ioc.util.StringUtils
import com.aagu.mvc.annotation.Controller
import java.util.concurrent.ConcurrentHashMap

class MvcRegistry : PackageScanner.Filter, FactoryPostProcessor {
    private val controllerList = ArrayList<Class<*>>()
    private lateinit var factory: AbstractBeanFactory

    override fun onFilter(clazz: Class<*>) {
        if (clazz.isAnnotationPresent(Controller::class.java)) {
            controllerList.add(clazz)
        }
    }

    override fun process(beanDefinitionMap: ConcurrentHashMap<String, BeanDefinition>) {
        for (clazz in controllerList) {
            val beanName = StringUtils.lowerCaseFirstChar(clazz.simpleName)
            val beanDef = GeneralBeanDefinition()
            beanDef.setBeanClass(clazz)
            factory.registerBeanDefinition(beanName, beanDef)
        }
    }

    override fun setBeanFactory(beanFactory: AbstractBeanFactory) {
        this.factory = beanFactory
    }

    fun registerHandlers(context: AbstractApplicationContext) {
        val dispatcherServlet = context.getBean(DispatcherServlet::class.java)
        dispatcherServlet.registerMapping(context)
    }

    fun getControllerClasses(): List<Class<*>> {
        return controllerList
    }
}