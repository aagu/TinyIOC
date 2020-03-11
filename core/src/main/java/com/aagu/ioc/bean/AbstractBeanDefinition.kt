package com.aagu.ioc.bean

abstract class AbstractBeanDefinition :BeanDefinition {
    private var scope = BeanDefinition.SCOPE_SINGLETON
    private var initMethodName: String? = null
    private var destroyMethodName: String? = null
    private var constructorArguments: Array<*>? = null
    private var propertyValues: List<PropertyValue>? = null

    override fun getScope(): String {
        return scope
    }

    fun setScope(scope: String) {
        this.scope = scope
    }

    override fun isSingleton(): Boolean {
        return scope == BeanDefinition.SCOPE_SINGLETON
    }

    override fun isPrototype(): Boolean {
        return scope == BeanDefinition.SCOPE_PROTOTYPE
    }

    override fun getInitMethodName(): String? {
        return initMethodName
    }

    fun setInitMethodName(methodName: String) {
        this.initMethodName = methodName
    }

    override fun getDestroyMethodName(): String? {
        return destroyMethodName
    }

    fun setDestroyMethodName(methodName: String) {
        this.destroyMethodName = methodName
    }

    override fun getConstructorArgumentValues(): Array<*>? {
        return constructorArguments
    }

    fun setConstructorArguments(args: Array<*>?) {
        this.constructorArguments = args
    }

    override fun getPropertyValues(): List<PropertyValue>? {
        return propertyValues
    }

    fun setPropertyValues(values: List<PropertyValue>?) {
        this.propertyValues = values
    }
}