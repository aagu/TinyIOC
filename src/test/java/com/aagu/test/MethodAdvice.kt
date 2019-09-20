package com.aagu.test

import com.aagu.aop.advice.BeforeAdvice
import com.aagu.aop.advice.ExceptionAdvice
import com.aagu.aop.annotation.AfterThrow
import com.aagu.aop.annotation.Before
import com.aagu.ioc.annotation.Bean
import java.lang.reflect.Method

@Bean
class MethodAdvice: BeforeAdvice, ExceptionAdvice {

    @AfterThrow("execution (* com.aagu.test.Bean1.init())")
    override fun afterThrow(exception: Exception?, method: Method, args: Array<Any>, target: Any) {
        println("error in executing ${target.javaClass.name}.${method.name}(${args})")
    }

    @Before("execution (* com.aagu.test.Bean1.doSomething())")
    override fun before(method: Method, args: Array<Any>, target: Any) {
        println("before method call")
    }
}