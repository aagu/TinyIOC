package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Value
import com.aagu.ioc.annotation.Wire
import com.aagu.test.Bean2

@Bean
class Bean3(private val bean2: Bean2) {
//    @Wire lateinit var bean2: Bean2
    @Value("hello") lateinit var str: String

    override fun toString(): String {
        return "Bean3: {bean2: $bean2, str: $str}"
    }
}