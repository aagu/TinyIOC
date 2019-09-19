package com.aagu.aop.advice

import java.lang.reflect.Method

interface AroundAdvice: Advice {
    fun around(method: Method, args: Array<Any>, target: Any)
}