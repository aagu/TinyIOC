package com.aagu.aop.proxy

import com.aagu.aop.advisor.Advisor
import com.aagu.ioc.bean.BeanFactory

class DefaultAopProxyFactory: AopProxyFactory {
    override fun createAopProxy(
        bean: Any,
        beanName: String,
        matchedAdvisors: List<Advisor>,
        beanFactory: BeanFactory
    ): AopProxy {
        return if (shouldUseJdkDynamicProxy(bean, beanName)) {
            JdkDynamicAopProxy(beanName, bean, matchedAdvisors, beanFactory)
        } else {
            CglibDynamicAopProxy(beanName, bean, matchedAdvisors, beanFactory)
        }
    }

    private fun shouldUseJdkDynamicProxy(bean: Any, beanName: String): Boolean {
        return false
    }
}