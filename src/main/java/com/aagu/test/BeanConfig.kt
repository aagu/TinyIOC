package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Config

@Config
class BeanConfig {
    @Bean
    fun bean4(): Bean4 {
        return Bean4("bean4")
    }

    @Bean
    fun bean5(): Bean4 {
        return Bean4("fake5")
    }
}