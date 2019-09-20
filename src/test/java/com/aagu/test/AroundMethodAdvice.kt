package com.aagu.test

import com.aagu.aop.advice.AroundAdvice
import com.aagu.aop.annotation.Around
import com.aagu.ioc.annotation.Bean
import java.lang.reflect.Method

@Bean
class AroundMethodAdvice: AroundAdvice {
    @Around("execution (* com.aagu.test.Bean1.doSomething())")
    override fun around(method: Method, args: Array<Any>, target: Any) {
        println("before method call")
        method.invoke(target, *args)
        println("after method call")
    }
}