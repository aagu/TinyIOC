package com.aagu.aop.proxy

import com.aagu.aop.advisor.Advisor
import com.aagu.aop.util.AopProxyUtils
import com.aagu.ioc.factory.BeanFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class JdkDynamicAopProxy(
    val beanName: String,
    val target: Any,
    private val matchedAdvisors: List<Advisor>,
    private val beanFactory: BeanFactory
): AopProxy, InvocationHandler {
    override fun getProxy(): Any {
        return this.getProxy(target.javaClass.classLoader)
    }

    override fun getProxy(classLoader: ClassLoader): Any {
        return Proxy.newProxyInstance(classLoader, target.javaClass.interfaces, this)
    }

    override fun invoke(proxy: Any, method: Method, args: Array<Any?>): Any? {
        return AopProxyUtils.applyAdvices(target, method, args, matchedAdvisors, proxy, beanFactory)
    }
}