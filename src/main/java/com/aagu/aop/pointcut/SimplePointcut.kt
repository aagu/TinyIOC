package com.aagu.aop.pointcut

import java.lang.reflect.Method

class SimplePointcut: Pointcut {
    override fun matchMethod(method: Method, targetClass: Class<*>): Boolean {
        return method.name == "doSomething"
    }

    override fun matchClass(targetClass: Class<*>): Boolean {
        return targetClass.simpleName == "Bean1"
    }
}