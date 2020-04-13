package com.aagu.aop.proxy

interface AopProxy {
    fun getProxy(): Any
    fun getProxy(classLoader: ClassLoader): Any
    fun getTarget(): Any
}