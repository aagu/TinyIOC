package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Wire
import com.aagu.test.Bean1

@Bean
class Bean2() {
    @Wire private var bean1: Bean1? = null

    constructor(bean1: Bean1) : this() {
        this.bean1 = bean1
    }

    override fun toString(): String {
        return "Bean2: {bean1: $bean1}"
    }


}