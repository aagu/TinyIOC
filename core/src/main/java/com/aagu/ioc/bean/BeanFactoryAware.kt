package com.aagu.ioc.bean

import com.aagu.ioc.factory.AbstractBeanFactory

interface BeanFactoryAware {
    fun setBeanFactory(factory: AbstractBeanFactory)
}