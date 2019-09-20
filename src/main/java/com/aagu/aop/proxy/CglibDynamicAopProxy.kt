package com.aagu.aop.proxy

import com.aagu.aop.advisor.Advisor
import com.aagu.aop.util.AopProxyUtils
import com.aagu.ioc.factory.BeanFactory
import com.aagu.ioc.factory.DefaultBeanFactory
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Constructor
import java.lang.reflect.Method

class CglibDynamicAopProxy(
    val beanName: String,
    val target: Any,
    private val matchedAdvisors: List<Advisor>,
    private val beanFactory: BeanFactory
    ): AopProxy, MethodInterceptor {
    override fun getProxy(): Any {
        return this.getProxy(target.javaClass.classLoader)
    }

    override fun getProxy(classLoader: ClassLoader): Any {
        val superClass = target.javaClass
        enhancer.setSuperclass(superClass)
        enhancer.setInterfaces(this.javaClass.interfaces)
        enhancer.setCallback(this)
        var constructor: Constructor<*>? = null
        try {
            constructor = superClass.getConstructor()
        } catch (e: NoSuchMethodException) {

        } catch (e: SecurityException) {
        }

        return if (constructor != null) {
            enhancer.create()
        } else {
            val bd = (beanFactory as DefaultBeanFactory).getBeanDefinition(beanName)
            enhancer.create(bd.getConstructor()!!.parameterTypes, bd.getConstructorArgumentValues())
        }
    }

    override fun intercept(proxy: Any, method: Method, args: Array<Any?>, methodProxy: MethodProxy): Any? {
        return AopProxyUtils.applyAdvices(target, method, args, matchedAdvisors, proxy, beanFactory)
    }

    companion object {
        private var enhancer = Enhancer()
    }
}