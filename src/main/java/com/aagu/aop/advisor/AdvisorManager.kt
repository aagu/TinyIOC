package com.aagu.aop.advisor

import com.aagu.aop.annotation.After
import com.aagu.aop.annotation.Around
import com.aagu.aop.annotation.Before
import com.aagu.ioc.bean.BeanDefinition
import com.aagu.ioc.factory.FactoryPostProcessor
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

class AdvisorManager: AdvisorRegistry, FactoryPostProcessor {
    private val advisors = ArrayList<Advisor>()

    override fun registerAdvisor(ad: Advisor) {
        advisors.add(ad)
    }

    override fun getAdvisors(): ArrayList<Advisor> {
        return advisors
    }

    override fun process(beanDefinitionMap: ConcurrentHashMap<String, BeanDefinition>) {
        for (beanDefs in beanDefinitionMap) {
            val beanClass = beanDefs.value.getBeanClass()
            if (beanClass != null) {
                val methods = beanClass.methods
                for (method in methods) {
                    processAdviceMethod(beanDefs.key, method)
                }
            }
        }
    }

    private fun processAdviceMethod(beanName: String, method: Method) {
        var expression = ""
        when {
            method.isAnnotationPresent(Before::class.java) -> expression = method.getAnnotation(Before::class.java).expression
            method.isAnnotationPresent(After::class.java) -> expression = method.getAnnotation(After::class.java).expression
            method.isAnnotationPresent(Around::class.java) -> expression = method.getAnnotation(Around::class.java).expression
        }
        if (expression.isNotEmpty()) {
            registerAdvisor(AspectJPointcutAdvisor(beanName, expression))
        }
    }
}