package com.aagu.ioc.factory

import com.aagu.ioc.bean.BeanPostProcessor
import com.aagu.ioc.exception.BeanNotFoundException
import java.io.Closeable

interface BeanFactory : Closeable {
    @Throws(BeanNotFoundException::class)
    fun <T> getBean(name: String): T

    @Throws(BeanNotFoundException::class)
    fun <T> getBean(clazz: Class<T>): T

    fun registerBeanPostProcessor(processor: BeanPostProcessor)

    fun registerFactoryPostProcessor(processor: FactoryPostProcessor)
}