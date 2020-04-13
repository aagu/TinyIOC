package com.aagu.aop.util

import com.aagu.aop.advice.*
import com.aagu.aop.advisor.Advisor
import com.aagu.aop.advisor.PointcutAdvisor
import com.aagu.aop.proxy.AopAdviceChainInvocation
import com.aagu.aop.proxy.AopProxy
import com.aagu.ioc.factory.BeanFactory
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

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
//        ): List<AdviceDelegate> {
        ): List<AdviceWrapper> {
            if (matchedAdvisors.isEmpty()) return emptyList()
//            val advices = ArrayList<AdviceDelegate>()
            val advices = ArrayList<AdviceWrapper>()
            for (ad in matchedAdvisors) {
                if (ad is PointcutAdvisor) {
                    if (ad.getPointcut().matchMethod(method, clazz)) {
//                        val adviceDelegate = createAdviceDelegate(ad, beanFactory)
//                        advices.add(adviceDelegate)
                        val adviceWrapper = createAdviceWrapper(ad, beanFactory)
                        advices.add(adviceWrapper)
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
//                BeforeAdvice::class.java -> return AdviceWrapper(advisor.getAdviceBeanName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
                ExceptionAdvice::class.java -> return AdviceDelegate(advisor.getAdviceBeanName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
            }
            throw RuntimeException("无法创建Advice代理对象，未知Advice类型!")
        }

        private fun createAdviceWrapper(advisor: Advisor, beanFactory: BeanFactory): AdviceWrapper {
            when (val adviceType = advisor.getAdviceType()) {
                AfterAdvice::class.java -> return AdviceWrapper(advisor.getAdviceBeanName(), advisor.getAdviceMethodName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
                AroundAdvice::class.java -> return AdviceWrapper(advisor.getAdviceBeanName(), advisor.getAdviceMethodName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
                BeforeAdvice::class.java -> return AdviceWrapper(advisor.getAdviceBeanName(), advisor.getAdviceMethodName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
                ExceptionAdvice::class.java -> return AdviceWrapper(advisor.getAdviceBeanName(), advisor.getAdviceMethodName(), beanFactory.getBean(advisor.getAdviceBeanName()), adviceType)
            }
            throw RuntimeException("无法创建Advice包装对象，未知Advice类型!")
        }

        fun equalsInProxy(aopProxy: AopProxy, otherProxy: AopProxy): Boolean {
            return (aopProxy == otherProxy ||
                    (equalsProxiedInterfaces(aopProxy, otherProxy)))
        }

        fun equalsProxiedInterfaces(aopProxy: AopProxy, otherProxy: AopProxy): Boolean {
            return Arrays.equals(aopProxy.getTarget().javaClass.interfaces, otherProxy.getTarget().javaClass.interfaces)
        }
    }
}