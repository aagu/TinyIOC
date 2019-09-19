package com.aagu.test

import com.aagu.aop.advice.AroundAdvice
import com.aagu.ioc.annotation.Bean
import java.lang.reflect.Method

@Bean
class AroundMethodAdvice: AroundAdvice {
    override fun around(method: Method, args: Array<Any>, target: Any) {
        println("before method call")
        method.invoke(target, *args)
        println("after method call")
    }
}