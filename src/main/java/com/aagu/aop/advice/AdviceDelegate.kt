package com.aagu.aop.advice

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

@Deprecated(message = "this class is deprecated", replaceWith = ReplaceWith("AdviceWrapper"))
class AdviceDelegate(val beanName: String, val target: Any, val adviceType: Class<*>): InvocationHandler {
    fun getDelegate(): Any {
        return getDelegate(target.javaClass.classLoader)
    }

    fun getDelegate(classLoader: ClassLoader): Any {
        return Proxy.newProxyInstance(classLoader, target.javaClass.interfaces, this)
    }

    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        return method.invoke(target, *args)
    }
}