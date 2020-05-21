package com.aagu.aop.proxy

import com.aagu.aop.advisor.Advisor
import com.aagu.aop.util.AopProxyUtils
import com.aagu.ioc.factory.BeanFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class JdkDynamicAopProxy(
    val beanName: String,
    private val target: Any,
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
        if (method.name == "equals" && method.declaringClass == Object::class.java) {
            return equals(args[0])
        } else if (method.name == "hashCode" && method.declaringClass == Object::class.java) {
            return hashCode()
        }
        return AopProxyUtils.applyAdvices(target, method, args, matchedAdvisors, proxy, beanFactory)
    }

    override fun getTarget(): Any {
        return target
    }

    override fun equals(other: Any?): Boolean {
        if (other == this) {
            return true
        }
        if (other == null) {
            return false
        }

        val otherProxy: JdkDynamicAopProxy
        otherProxy = when {
            other is JdkDynamicAopProxy -> {
                other
            }
            Proxy.isProxyClass(other.javaClass) -> {
                val ih = Proxy.getInvocationHandler(other)
                if (ih !is JdkDynamicAopProxy) {
                    return false
                }
                ih
            }
            else -> {
                return false
            }
        }

        return AopProxyUtils.equalsInProxy(this, otherProxy)
    }

    override fun hashCode(): Int {
        return JdkDynamicAopProxy::class.java.hashCode() * 13 + target.hashCode()
    }
}