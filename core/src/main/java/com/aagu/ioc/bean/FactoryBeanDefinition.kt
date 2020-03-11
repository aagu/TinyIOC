package com.aagu.ioc.bean

import com.aagu.ioc.exception.NotSupportedException
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * this define bean produce by factory
 */
class FactoryBeanDefinition : AbstractBeanDefinition() {
    private var factoryBeanName: String? = null
    private var factoryMethodName: String? = null
    private var factoryMethod: Method? = null
    var allowGenericType: Boolean = false
    private var targetClass: Class<*>? = null

    override fun getBeanClass(): Class<*>? {
        return null
    }

    override fun getFactoryBeanName(): String? {
        return factoryBeanName
    }

    fun setFactoryBeanName(beanName: String) {
        this.factoryBeanName = beanName
    }

    override fun getFactoryMethodName(): String? {
        return factoryMethodName
    }

    fun setFactoryMethodName(methodName: String) {
        this.factoryMethodName = methodName
    }

    override fun getConstructor(): Constructor<*>? {
        return null
    }

    override fun getFactoryMethod(): Method? {
       return factoryMethod
    }

    override fun setFactoryMethod(method: Method) {
        this.factoryMethod = method
    }

    override fun setConstructor(constructor: Constructor<*>) {
        throw NotSupportedException("bean factory has no constructor")
    }

    fun setTargetClass(clazz: Class<*>) {
        this.targetClass = clazz
    }

    fun getTarGetClass(): Class<*>? {
        return targetClass
    }
}