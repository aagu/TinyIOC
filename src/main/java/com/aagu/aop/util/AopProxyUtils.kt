package com.aagu.aop.util

import com.aagu.aop.advisor.Advisor
import com.aagu.aop.advisor.PointcutAdvisor
import com.aagu.aop.proxy.AopAdviceChainInvocation
import com.aagu.ioc.factory.BeanFactory
import java.lang.reflect.Method

class AopProxyUtils {
    companion object {
        fun applyAdvices(
            target: Any,
            method: Method,
            args: Array<Any>,
            matchedAdvisors: List<Advisor>,
            proxy: Any,
            beanFactory: BeanFactory
        ): Any? {
            val advices = getShouldApplyAdvices(target.javaClass, method, matchedAdvisors, beanFactory)
            return if (advices.isEmpty()) {
                method.invoke(target, *args)
            } else {
                val chain = AopAdviceChainInvocation(proxy, target, method, args, advices)
                chain.invoke()
            }
        }

        private fun getShouldApplyAdvices(
            clazz: Class<Any>,
            method: Method,
            matchedAdvisors: List<Advisor>,
            beanFactory: BeanFactory
        ): List<Any> {
            if (matchedAdvisors.isEmpty()) return emptyList()
            val advices = ArrayList<Any>()
            for (ad in matchedAdvisors) {
                if (ad is PointcutAdvisor) {
                    if (ad.getPointcut().matchMethod(method, clazz)) {
                        advices.add(beanFactory.getBean(ad.getAdviceBeanName()))
                    }
                }
            }
            return advices
        }
    }
}