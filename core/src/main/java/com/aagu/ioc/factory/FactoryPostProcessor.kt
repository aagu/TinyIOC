package com.aagu.ioc.factory

import com.aagu.ioc.bean.BeanDefinition
import java.util.concurrent.ConcurrentHashMap

interface FactoryPostProcessor {
    fun process(beanDefinitionMap: ConcurrentHashMap<String, BeanDefinition>)
    fun setBeanFactory(beanFactory: AbstractBeanFactory) {}
}