package com.aagu.data.proxy

import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

class RepositoryProxyFactory<T>(private val repositoryInterface: Class<T>) {
    private val methodCache = ConcurrentHashMap<Method, RepositoryMethod>()

    @Suppress("UNCHECKED_CAST")
    protected fun newInstance(repositoryProxy: RepositoryProxy<T>): T {
        return Proxy.newProxyInstance(repositoryInterface.classLoader,
            arrayOf(repositoryInterface), repositoryProxy) as T
    }

    fun newInstance(): T {
        val repositoryProxy = RepositoryProxy<T>(methodCache)
        return newInstance(repositoryProxy)
    }

    fun cacheMethod(method: Method, repositoryMethod: RepositoryMethod) {
        methodCache[method] = repositoryMethod
    }
}