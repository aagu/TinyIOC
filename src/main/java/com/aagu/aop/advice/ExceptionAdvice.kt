package com.aagu.aop.advice

import java.lang.reflect.Method

interface ExceptionAdvice: Advice {
    fun afterThrow(exception: Exception?, method: Method, args: Array<Any?>, target: Any)
}