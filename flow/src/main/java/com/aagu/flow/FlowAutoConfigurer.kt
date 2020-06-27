package com.aagu.flow

import com.aagu.ioc.context.AutoConfigurer
import com.aagu.ioc.context.support.PropertiesApplicationContext

class FlowAutoConfigurer : AutoConfigurer {
    override fun beforeContextRefresh(context: PropertiesApplicationContext) {
        context.addScanPackage(FLOW_SUPPORT_PACKAGE_NAME)
    }

    override fun afterContextRefresh(context: PropertiesApplicationContext) {
    }

    companion object {
        private const val FLOW_SUPPORT_PACKAGE_NAME = "com.aagu.flow"
    }
}