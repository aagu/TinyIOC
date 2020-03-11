package com.aagu.data

import com.aagu.data.session.SessionManager
import com.aagu.ioc.context.AutoConfigurer
import com.aagu.ioc.context.support.PropertiesApplicationContext

class DataAutoConfigurer : AutoConfigurer {
    private val sessionManager = SessionManager()

    override fun beforeContextRefresh(context: PropertiesApplicationContext) {
        context.addScanPackage(DATA_SUPPORT_PACKAGE_NAME)
        context.addOnScanFilter(sessionManager)
        context.registerFactoryPostProcessor(sessionManager)
    }

    override fun afterContextRefresh(context: PropertiesApplicationContext) {
    }

    companion object {
        private const val DATA_SUPPORT_PACKAGE_NAME = "com.aagu.data"
    }
}