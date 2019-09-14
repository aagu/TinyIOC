package com.aagu.ioc.bean

import com.aagu.ioc.bean.BeanDefinition
import com.aagu.ioc.bean.PropertyValue
import com.aagu.ioc.util.StringUtils
import java.lang.reflect.Constructor
import java.lang.reflect.Method

class GeneralBeanDefinition: BeanDefinition {
    private var beanClass: Class<*>? = null
    private var scope = BeanDefinition.SCOPE_SINGLETON
    private var factoryBeanName: String? = null
    private var factoryMethodName: String? = null
    private var initMethodName: String? = null
    private var destroyMethodName: String? = null
    private var constructorArguments: Array<*>? = null
    private var constructor: Constructor<*>? = null
    private var factoryMethod: Method? = null
    private var propertyValues: List<PropertyValue>? = null

    fun setScope(scope: String) {
        if (StringUtils.isNotEmpty(scope)) {
            this.scope = scope
        }
    }

    fun setBeanClass(clazz: Class<*>) {
        this.beanClass = clazz
    }

    fun setFactoryBeanName(factoryBeanName: String) {
        this.factoryBeanName = factoryBeanName
    }

    fun setFactoryMethodName(factoryMethodName: String) {
        this.factoryMethodName = factoryMethodName
    }

    fun setInitMethodName(initMethodName: String) {
        this.initMethodName = initMethodName
    }

    fun setDestroyMethodName(destroyMethodName: String) {
        this.destroyMethodName = destroyMethodName
    }

    fun setConstructorArguments(arguments: Array<*>) {
        this.constructorArguments = arguments
    }

    fun setPropertyValues(values: List<PropertyValue>) {
        this.propertyValues = values
    }

    override fun getScope(): String {
        return scope
    }

    override fun isSingleton(): Boolean {
        return scope == BeanDefinition.SCOPE_SINGLETON
    }

    override fun isPrototype(): Boolean {
        return scope == BeanDefinition.SCOPE_PROTOTYPE
    }

    override fun getFactoryBeanName(): String? {
        return factoryBeanName
    }

    override fun getFactoryMethodName(): String? {
        return factoryMethodName
    }

    override fun getInitMethodName(): String? {
        return initMethodName
    }

    override fun getDestroyMethodName(): String? {
        return destroyMethodName
    }

    override fun getBeanClass(): Class<*>? {
        return beanClass
    }

    override fun getConstructorArgumentValues(): Array<*>? {
        return constructorArguments
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

    override fun getPropertyValues(): List<PropertyValue>? {
        return propertyValues
    }
}