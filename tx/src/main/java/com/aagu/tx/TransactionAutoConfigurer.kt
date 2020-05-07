package com.aagu.tx

import com.aagu.ioc.context.AutoConfigurer
import com.aagu.ioc.context.support.PropertiesApplicationContext

class TransactionAutoConfigurer : AutoConfigurer {
    override fun beforeContextRefresh(context: PropertiesApplicationContext) {
        context.addScanPackage(TX_SUPPORT_PACKAGE_NAME)
    }

    override fun afterContextRefresh(context: PropertiesApplicationContext) {
    }

    companion object {
        const val TX_SUPPORT_PACKAGE_NAME = "com.aagu.tx"
    }
}