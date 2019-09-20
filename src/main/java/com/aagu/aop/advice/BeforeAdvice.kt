package com.aagu.aop.advice

import java.lang.reflect.Method

interface BeforeAdvice: Advice {
    fun before(method: Method, args: Array<Any?>, target: Any)
}