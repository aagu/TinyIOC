package com.aagu.data.proxy

import com.aagu.data.annotation.Param
import com.aagu.data.sql.Variable
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.jvm.kotlinFunction

class RepositoryProxyFactory<T>(private val repositoryInterface: Class<T>) {
    private val methodCache = ConcurrentHashMap<Method, RepositoryMethod>()

    @Suppress("UNCHECKED_CAST")
    private fun newInstance(repositoryProxy: RepositoryProxy<T>): T {
        return Proxy.newProxyInstance(repositoryInterface.classLoader,
            arrayOf(repositoryInterface), repositoryProxy) as T
    }

    fun newInstance(): T {
        val repositoryProxy = RepositoryProxy<T>(methodCache)
        return newInstance(repositoryProxy)
    }

    fun cacheMethod(method: Method, repositoryMethod: RepositoryMethod) {
        methodCache[method] = repositoryMethod
        processArgParameters(method, repositoryMethod)
    }

    private fun processArgParameters(method: Method, repositoryMethod: RepositoryMethod) {
        val parameters = method.parameters
        val kFunction = method.kotlinFunction
        val kParameters = kFunction?.parameters
        repositoryMethod.argVariables = Array(parameters.size) { Variable() }
        for ((idx, p) in parameters.withIndex()) {
            repositoryMethod.argVariables[idx].type = p.type
            if (p.isAnnotationPresent(Param::class.java)) {
                repositoryMethod.argVariables[idx].name = p.getAnnotation(Param::class.java).value
            } else {
                if (kParameters != null) {
                    // this first element in list should be this callable function
                    repositoryMethod.argVariables[idx].name = kParameters[idx+1].name
                } else {
                    repositoryMethod.argVariables[idx].name = p.name
                }
            }
        }
    }
}