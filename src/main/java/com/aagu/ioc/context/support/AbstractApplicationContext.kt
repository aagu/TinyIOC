package com.aagu.ioc.context.support

import com.aagu.ioc.context.ApplicationContext
import com.aagu.ioc.factory.BeanFactory

abstract class AbstractApplicationContext : ApplicationContext {
    @Throws(Exception::class)
    fun refresh() {
        val beanFactory: BeanFactory = obtainFreshBeanFactory()
    }

    @Throws(Exception::class)
    protected fun obtainFreshBeanFactory(): BeanFactory {
        refreshBeanFactory()
        return getBeanFactory()
    }

    @Throws(Exception::class)
    protected abstract fun refreshBeanFactory()

    abstract fun getBeanFactory(): BeanFactory
}