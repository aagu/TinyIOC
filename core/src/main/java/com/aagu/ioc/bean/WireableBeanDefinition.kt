package com.aagu.ioc.bean

import java.lang.reflect.Constructor
import java.lang.reflect.Method

class WireableBeanDefinition : AbstractBeanDefinition() {
    private var beanClass: Class<*>? = null
    private var constructor: Constructor<*>? = null
    private val wireList: ArrayList<PropertyValue> = ArrayList()

    fun addWireProperty(propertyValue: PropertyValue) {
        wireList.add(propertyValue);
    }

    fun getWireProperties(): List<PropertyValue> {
        return wireList
    }

    override fun getBeanClass(): Class<*>? {
        return beanClass
    }

    fun setBeanClass(clazz: Class<*>) {
        this.beanClass = clazz
    }

    override fun getFactoryBeanName(): String? {
        return null
    }

    override fun getFactoryMethodName(): String? {
        throw UnsupportedOperationException()
    }

    override fun getConstructor(): Constructor<*>? {
        return constructor
    }

    override fun setConstructor(constructor: Constructor<*>) {
        this.constructor = constructor
    }

    override fun getFactoryMethod(): Method? {
        return null
    }

    override fun setFactoryMethod(method: Method) {
        throw UnsupportedOperationException()
    }
}