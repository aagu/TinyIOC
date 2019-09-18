package com.aagu.test

import com.aagu.ioc.annotation.DestroyMethod
import com.aagu.ioc.annotation.InitMethod
import com.aagu.ioc.annotation.Value

class Bean4(val string: String) {
    @Value("done") lateinit var status: String

    @InitMethod fun init() {
        println("init of bean4")
    }

    @DestroyMethod fun destroy() {
        println("destroy of bean4")
    }

    override fun toString(): String {
        return "Bean4: {string: $string, status: $status}"
    }
}