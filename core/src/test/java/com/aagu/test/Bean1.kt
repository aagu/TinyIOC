package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Value

@Bean(scope = "prototype")
open class Bean1 {
    @Value("#{hello}:147") var text: Int = 0;

    open fun doSomething() {
        println("this is bean1")
    }

    open fun doAnotherThing() {
        println("this is a function")
    }

    //    @InitMethod
    open fun init() {
        println("init of bean1")
        throw RuntimeException("something went wrong")
    }

    //    @DestroyMethod
    fun destroy() {
        println("destroy of bean1")
    }

    override fun toString(): String {
        return "Bean1: {text: $text}"
    }
}