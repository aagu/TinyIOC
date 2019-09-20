package com.aagu.aop.proxy

import com.aagu.aop.advisor.Advisor
import com.aagu.aop.advisor.PointcutAdvisor
import com.aagu.ioc.bean.BeanPostProcessor
import com.aagu.ioc.factory.BeanFactory
import java.lang.reflect.Method

class AdvisorAutoProxyCreator(private val beanFactory: BeanFactory): BeanPostProcessor {
    private lateinit var advisors: ArrayList<Advisor>

    fun setAdvisors(advisors: ArrayList<Advisor>) {
        this.advisors = advisors
    }

    fun getAdvisors(): ArrayList<Advisor> {
        return advisors
    }

    override fun postProcessAfterInitialization(beanName: String, bean: Any): Any {
        val matchedAdvisors = getMatchedAdvisor(bean, beanName)
        var localBean = bean
        if (matchedAdvisors.isNotEmpty()) {
            localBean = createProxy(localBean, beanName, matchedAdvisors)
        }
        return localBean
    }

    private fun getMatchedAdvisor(bean: Any, beanName: String): List<Advisor> {
        if (advisors.isEmpty()) return emptyList()
        val beanClass = bean.javaClass
        val methods = beanClass.methods
        val matchedAdvisors = ArrayList<Advisor>()
        for (ad in advisors) {
            if (ad is PointcutAdvisor) {
                if (isPointcutMatchBean(ad, beanClass, methods)) {
                    matchedAdvisors.add(ad)
                }
            }
        }
        return matchedAdvisors
    }

    private fun isPointcutMatchBean(
        pa: PointcutAdvisor,
        beanClass: Class<Any>,
        methods: Array<Method>
    ): Boolean {
        val pointcut = pa.getPointcut()
        if (!pointcut.matchClass(beanClass)) {
            return false
        }
        for (method in methods) {
            if (pointcut.matchMethod(method, beanClass)) {
                return true
            }
        }
        return false
    }

    private fun createProxy(bean: Any, beanName: String, advisors: List<Advisor>): Any {
        return AopProxyFactory.getDefaultAopProxyFactory().createAopProxy(bean, beanName, advisors, beanFactory).getProxy()
    }
}