package com.aagu.aop.proxy

import com.aagu.aop.advisor.Advisor
import com.aagu.ioc.bean.BeanFactory

interface AopProxyFactory {
    fun createAopProxy(bean: Any, beanName: String, matchedAdvisors: List<Advisor>, beanFactory: BeanFactory): AopProxy

    companion object {
        fun getDefaultAopProxyFactory(): AopProxyFactory {
            return DefaultAopProxyFactory()
        }
    }
}