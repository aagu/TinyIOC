package com.aagu.ioc.bean

class BeanReference(private val beanName: String) {
    fun getBeanName(): String {
        return this.beanName
    }
}