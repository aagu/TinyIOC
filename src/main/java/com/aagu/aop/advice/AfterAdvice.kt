package com.aagu.aop.advice

import java.lang.reflect.Method

interface AfterAdvice: Advice {
    fun after(returnValue: Any?, method: Method, args: Array<Any>, target: Any)
}