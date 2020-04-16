package com.aagu.ioc.bean

import com.aagu.ioc.util.StringUtils
import java.lang.reflect.Constructor
import java.lang.reflect.Method

interface BeanDefinition {
    fun getBeanClass(): Class<*>?

    fun getScope(): String
    fun isSingleton(): Boolean
    fun isPrototype(): Boolean
    fun getFactoryBeanName(): String?
    fun getFactoryMethodName(): String?
    fun getInitMethodName(): String?
    fun getDestroyMethodName(): String?
    fun getConstructorArguments(): Array<*>?
    fun getConstructor(): Constructor<*>?
    fun setConstructor(constructor: Constructor<*>)
    fun getFactoryMethod(): Method?
    fun setFactoryMethod(method: Method)
    fun getPropertyValues(): List<PropertyValue>?

    fun validate(): Boolean {
        if (getBeanClass() == null) {
            if (StringUtils.isEmpty(getFactoryBeanName()) || StringUtils.isEmpty(getFactoryMethodName())) {
                return false
            }
        }

        if (getBeanClass() != null && StringUtils.isNotEmpty(getFactoryBeanName())) return false
        return true
    }

    companion object{
        const val SCOPE_SINGLETON = "singleton"
        const val SCOPE_PROTOTYPE = "prototype"
    }
}