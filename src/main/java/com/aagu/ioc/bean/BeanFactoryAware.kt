package com.aagu.ioc.bean

import com.aagu.ioc.factory.DefaultBeanFactory

interface BeanFactoryAware {
    fun setBeanFactory(factory: DefaultBeanFactory)
}