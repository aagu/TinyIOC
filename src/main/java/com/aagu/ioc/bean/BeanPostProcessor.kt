package com.aagu.ioc.bean

interface BeanPostProcessor {
    fun postProcessBeforeInitialization(beanName: String, bean: Any): Any {
        return bean
    }

    fun postProcessAfterInitialization(beanName: String, bean: Any): Any {
        return bean
    }
}