package com.aagu.data.proxy

import java.io.Serializable
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class RepositoryProxy<T>(private val methodCache: Map<Method, RepositoryMethod>) : InvocationHandler, Serializable{

    override fun invoke(target: Any, method: Method, args: Array<out Any>?): Any? {
        if (java.lang.Object::class.java == method.declaringClass) {
            return method.invoke(target, args)
        }
        val repositoryMethod: RepositoryMethod = cachedRepositoryMethod(method)!!
        return repositoryMethod.execute(args)
    }

    private fun cachedRepositoryMethod(method: Method): RepositoryMethod? {
        return methodCache[method]
    }
}