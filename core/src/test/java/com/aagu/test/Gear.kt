package com.aagu.test

import com.aagu.ioc.annotation.Bean

@Bean
open class Gear(private val bean1: Bean1) {
    override fun toString(): String {
        return "I'm a gear"
    }

    open fun getBean1(): Bean1 {
        return bean1
    }

    open fun whoAmI(): String {
        return "Gear"
    }
}