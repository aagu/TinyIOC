package com.aagu.ioc.bean

import com.aagu.ioc.exception.BeanNotFoundException

interface BeanFactory {
    @Throws(BeanNotFoundException::class)
    fun <T> getBean(name: String): T

    @Throws(BeanNotFoundException::class)
    fun <T> getBean(clazz: Class<T>): T

    fun registerBeanPostProcessor(processor: BeanPostProcessor)
}