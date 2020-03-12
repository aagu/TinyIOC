package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Config

@Config
class BeanFactory {
    @Bean("gear")
    fun makeGear(): Gear {
        return Gear()
    }
}