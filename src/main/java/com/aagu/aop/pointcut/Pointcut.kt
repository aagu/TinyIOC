package com.aagu.aop.pointcut

import java.lang.reflect.Method

interface Pointcut {
    fun matchClass(targetClass: Class<*>): Boolean

    fun matchMethod(method: Method, targetClass: Class<*>): Boolean
}