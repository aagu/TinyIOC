package com.aagu.aop.proxy

import com.aagu.aop.advisor.Advisor
import com.aagu.aop.util.AopProxyUtils
import com.aagu.ioc.bean.BeanDefinitionRegistry
import com.aagu.ioc.factory.BeanFactory
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Constructor
import java.lang.reflect.Method

class CglibDynamicAopProxy(
    val beanName: String,
    private val target: Any,
    private val matchedAdvisors: List<Advisor>,
    private val beanFactory: BeanFactory
    ): AopProxy, MethodInterceptor {
    override fun getProxy(): Any {
        return this.getProxy(target.javaClass.classLoader)
    }

    override fun getProxy(classLoader: ClassLoader): Any {
        val superClass = target.javaClass
        enhancer.setSuperclass(superClass)
        enhancer.setInterceptDuringConstruction(false)
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
            val bd = (beanFactory as BeanDefinitionRegistry).getBeanDefinition(beanName)
            enhancer.create(bd.getConstructor()!!.parameterTypes, bd.getConstructorArgumentValues())
        }
    }

    override fun intercept(proxy: Any, method: Method, args: Array<Any?>, methodProxy: MethodProxy): Any? {
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
        return (this === other || other is CglibDynamicAopProxy && AopProxyUtils.equalsInProxy(this, other))
    }

    override fun hashCode(): Int {
        return CglibDynamicAopProxy::class.java.hashCode() * 13 + target.hashCode()
    }

    companion object {
        private var enhancer = Enhancer()
    }
}