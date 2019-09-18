package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Wire
import com.aagu.test.Bean1

@Bean
class Bean2() {
    @Wire private var bean: Bean5? = null

    override fun toString(): String {
        return "Bean2: {bean5: ${bean?.whoAmI()}}"
    }


}