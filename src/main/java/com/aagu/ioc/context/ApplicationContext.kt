package com.aagu.ioc.context

import com.aagu.ioc.bean.BeanDefinitionRegistry
import com.aagu.ioc.factory.BeanFactory

interface ApplicationContext : BeanFactory, BeanDefinitionRegistry {

}