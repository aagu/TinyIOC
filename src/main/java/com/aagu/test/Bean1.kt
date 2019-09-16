package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.DestroyMethod
import com.aagu.ioc.annotation.InitMethod
import com.aagu.ioc.annotation.Value
import kotlin.properties.Delegates

@Bean(scope = "prototype")
class Bean1 {
    @Value("#{hello}") var text: Int = 0;

    fun doSomething() {
        println("${System.currentTimeMillis()} $this")
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