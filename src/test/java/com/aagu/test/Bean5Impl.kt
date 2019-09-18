package com.aagu.test

import com.aagu.ioc.annotation.Bean

@Bean
class Bean5Impl: Bean5 {
    override fun whoAmI():String {
        return this.javaClass.simpleName
    }
}