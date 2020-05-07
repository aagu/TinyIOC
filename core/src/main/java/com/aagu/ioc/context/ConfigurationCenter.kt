package com.aagu.ioc.context

import com.aagu.aop.advisor.AdvisorManager
import com.aagu.aop.proxy.AdvisorAutoProxyCreator
import com.aagu.ioc.context.support.PropertiesApplicationContext
import com.aagu.ioc.util.PropertyLoader

/**
 * 配置中心
 * 在此处理各模块的自动配置
 */
class ConfigurationCenter(private val context: PropertiesApplicationContext) {
    private val autoConfigurers = ArrayList<AutoConfigurer>()

    init {
        if (PropertyLoader.getBooleanProperty("enable-aop", false)) {
            autoConfigurers.add(AopAutoConfigurer())
        }
        if (PropertyLoader.getBooleanProperty("enable-data", false)) {
            val configurer = Class.forName(DATA_SUPPORT_CONFIGURER).newInstance() as AutoConfigurer
            autoConfigurers.add(configurer)
        }
        if (PropertyLoader.getBooleanProperty("enable-web", false)) {
            val configurer = Class.forName(MVC_SUPPORT_CONFIGURER).newInstance() as AutoConfigurer
            autoConfigurers.add(configurer)
        }
        if (PropertyLoader.getBooleanProperty("enable-tx", true)) {
            val configurer = Class.forName(TX_SUPPORT_CONFIGURER).newInstance() as AutoConfigurer
            autoConfigurers.add(configurer)
        }
    }

    fun configureBeforeContextRefresh() {
        for (configurer in autoConfigurers) {
            configurer.beforeContextRefresh(context)
        }
    }

    fun configureAfterContextRefresh() {
        for (configurer in autoConfigurers) {
            configurer.afterContextRefresh(context)
        }
    }

    // AOP的自动配置，因为AOP也在core包，就写在这里了
    class AopAutoConfigurer : AutoConfigurer {
        private val advisorManager = AdvisorManager()

        override fun beforeContextRefresh(context: PropertiesApplicationContext) {
            context.addOnScanFilter(advisorManager)
            context.registerFactoryPostProcessor(advisorManager)
            val advisorAutoProxyCreator = AdvisorAutoProxyCreator(context)
            advisorAutoProxyCreator.setAdvisors(advisorManager.getAdvisors())
            context.registerBeanPostProcessor(advisorAutoProxyCreator)
        }

        override fun afterContextRefresh(context: PropertiesApplicationContext) {
        }
    }

    companion object {
        const val DATA_SUPPORT_CONFIGURER = "com.aagu.data.DataAutoConfigurer"
        const val MVC_SUPPORT_CONFIGURER = "com.aagu.mvc.MvcAutoConfigurer"
        const val TX_SUPPORT_CONFIGURER = "com.aagu.tx.TransactionAutoConfigurer"
    }
}