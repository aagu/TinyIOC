package com.aagu.ioc.context

import com.aagu.ioc.context.support.PropertiesApplicationContext

interface AutoConfigurer {
    fun beforeContextRefresh(context: PropertiesApplicationContext)

    fun afterContextRefresh(context: PropertiesApplicationContext)
}