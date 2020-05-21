package com.aagu.ioc.bean

class BeanReference(private val beanName: String) {
    private var referenceClass: Class<*>? = null

    fun getBeanName(): String {
        return this.beanName
    }

    fun getReferenceClass(): Class<*>? {
        return referenceClass
    }

    fun setReferenceClass(clazz: Class<*>?) {
        this.referenceClass = clazz
    }
}