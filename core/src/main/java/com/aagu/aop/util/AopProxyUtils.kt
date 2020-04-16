package com.aagu.aop.util

import com.aagu.aop.advice.AdviceWrapper
import com.aagu.aop.advisor.Advisor
import com.aagu.aop.advisor.PointcutAdvisor
import com.aagu.aop.proxy.AopAdviceChainInvocation
import com.aagu.aop.proxy.AopProxy
import com.aagu.ioc.factory.BeanFactory
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList

object AopProxyUtils {
    private val regex = Regex("^@annotation\\([A-Za-z]\\w+(\\.\\w+)*\\)$")

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

    fun isAnnotationExpression(expression: String): Boolean {
        return regex.matches(expression)
    }

    private fun getShouldApplyAdvices(
        clazz: Class<Any>,
        method: Method,
        matchedAdvisors: List<Advisor>,
        beanFactory: BeanFactory
    ): List<AdviceWrapper> {
        if (matchedAdvisors.isEmpty()) return emptyList()
        val advices = ArrayList<AdviceWrapper>()
        for (ad in matchedAdvisors) {
            if (ad is PointcutAdvisor) {
                if (ad.getPointcut().matchMethod(method, clazz)) {
                    val adviceWrapper = AdviceWrapper(ad, beanFactory.getBean(ad.getAdviceBeanName()))
                    advices.add(adviceWrapper)
                }
            }
        }
        advices.sort()
        return advices
    }

    fun equalsInProxy(aopProxy: AopProxy, otherProxy: AopProxy): Boolean {
        return (aopProxy == otherProxy ||
                (equalsProxiedInterfaces(aopProxy, otherProxy)))
    }

    fun equalsProxiedInterfaces(aopProxy: AopProxy, otherProxy: AopProxy): Boolean {
        return Arrays.equals(aopProxy.getTarget().javaClass.interfaces, otherProxy.getTarget().javaClass.interfaces)
    }
}