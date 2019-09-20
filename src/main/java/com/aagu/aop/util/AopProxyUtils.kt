package com.aagu.aop.util

import com.aagu.aop.advice.*
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
            args: Array<Any?>,
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
        ): List<AdviceDelegate> {
            if (matchedAdvisors.isEmpty()) return emptyList()
            val advices = ArrayList<AdviceDelegate>()
            for (ad in matchedAdvisors) {
                if (ad is PointcutAdvisor) {
                    if (ad.getPointcut().matchMethod(method, clazz)) {
                        val adviceDelegate = createAdviceDelegate(ad, beanFactory)
                        advices.add(adviceDelegate)
                    }
                }
            }
            return advices
        }

        private fun createAdviceDelegate(advisor: Advisor, beanFactory: BeanFactory): AdviceDelegate {
            when (val adviceType = advisor.getAdviceType()) {
                AfterAdvice::class.java -> return AdviceDelegate(advisor.getAdviceBeanName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
                AroundAdvice::class.java -> return AdviceDelegate(advisor.getAdviceBeanName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
                BeforeAdvice::class.java -> return AdviceDelegate(advisor.getAdviceBeanName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
                ExceptionAdvice::class.java -> return AdviceDelegate(advisor.getAdviceBeanName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
            }
            throw RuntimeException("无法创建Advice代理对象，未知Advice类型!")
        }
    }
}