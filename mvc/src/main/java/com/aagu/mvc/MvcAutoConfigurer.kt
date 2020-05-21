package com.aagu.mvc

import com.aagu.ioc.context.AutoConfigurer
import com.aagu.ioc.context.support.PropertiesApplicationContext

class MvcAutoConfigurer : AutoConfigurer {
    private val mvcRegistry = MvcRegistry()

    override fun beforeContextRefresh(context: PropertiesApplicationContext) {
        context.addOnScanFilter(mvcRegistry)
        context.addScanPackage(MVC_SUPPORT_PACKAGE_NAME)
        context.registerFactoryPostProcessor(mvcRegistry)
    }

    override fun afterContextRefresh(context: PropertiesApplicationContext) {
        mvcRegistry.registerHandlers(context)
    }

    companion object {
        private const val MVC_SUPPORT_PACKAGE_NAME = "com.aagu.mvc"
    }
}