package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.DestroyMethod
import com.aagu.ioc.annotation.InitMethod
import com.aagu.ioc.annotation.Value
import kotlin.properties.Delegates

@Bean(scope = "prototype")
open class Bean1 {
    @Value("#{hello}:147") var text: Int = 0;

    open fun doSomething() {
        println("this is bean1")
    }

//    @InitMethod
    fun init() {
        println("init of bean1")
    }

//    @DestroyMethod
    fun destroy() {
        println("destroy of bean1")
    }

    override fun toString(): String {
        return "Bean1: {text: $text}"
    }
}