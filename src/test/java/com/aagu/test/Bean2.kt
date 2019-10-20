package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Wire
import com.aagu.ioc.bean.BeanFactoryAware
import com.aagu.ioc.bean.BeanNameAware
import com.aagu.ioc.factory.DefaultBeanFactory

@Bean
class Bean2: BeanNameAware, BeanFactoryAware {
    @Wire private var bean: Bean5? = null

    override fun toString(): String {
        return "Bean2: {bean5: ${bean?.whoAmI()}}"
    }

    override fun setBeanName(beanName: String) {
        println("bean name in factory $beanName")
    }

    override fun setBeanFactory(factory: DefaultBeanFactory) {
        println("bean factory $factory")
    }
}