package com.aagu.ioc.bean

import java.lang.reflect.Constructor
import java.lang.reflect.Method

class GeneralBeanDefinition: AbstractBeanDefinition() {
    private var beanClass: Class<*>? = null
    private var factoryBeanName: String? = null
    private var factoryMethodName: String? = null
    private var constructor: Constructor<*>? = null
    private var factoryMethod: Method? = null

    fun setBeanClass(clazz: Class<*>) {
        this.beanClass = clazz
    }

    fun setFactoryBeanName(factoryBeanName: String) {
        this.factoryBeanName = factoryBeanName
    }

    fun setFactoryMethodName(factoryMethodName: String) {
        this.factoryMethodName = factoryMethodName
    }

    override fun getFactoryBeanName(): String? {
        return factoryBeanName
    }

    override fun getFactoryMethodName(): String? {
        return factoryMethodName
    }

    override fun getBeanClass(): Class<*>? {
        return beanClass
    }

    override fun getConstructor(): Constructor<*>? {
        return constructor
    }

    override fun setConstructor(constructor: Constructor<*>) {
        this.constructor = constructor
    }

    override fun getFactoryMethod(): Method? {
        return factoryMethod
    }

    override fun setFactoryMethod(method: Method) {
        this.factoryMethod = method
    }
}