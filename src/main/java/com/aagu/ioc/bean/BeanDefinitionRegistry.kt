package com.aagu.ioc.bean

import com.aagu.ioc.bean.BeanDefinition

interface BeanDefinitionRegistry {
    /**
     * 注册bean
     * @param name beanName
     * @param definition beanDefinition
     */
    fun registerBeanDefinition(name: String, definition: BeanDefinition)

    fun getBeanDefinition(name: String): BeanDefinition

    fun containsBeanDefinition(name: String): Boolean
}