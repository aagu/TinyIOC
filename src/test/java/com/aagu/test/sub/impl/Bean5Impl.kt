package com.aagu.test.sub.impl

import com.aagu.ioc.annotation.Bean
import com.aagu.test.sub.Bean5

@Bean
class Bean5Impl: Bean5 {
    override fun whoAmI():String {
        return this.javaClass.simpleName
    }
}