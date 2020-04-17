package com.aagu.aop.advisor

import com.aagu.aop.advice.*
import com.aagu.aop.annotation.*
import com.aagu.ioc.bean.BeanDefinition
import com.aagu.ioc.bean.GeneralBeanDefinition
import com.aagu.ioc.factory.AbstractBeanFactory
import com.aagu.ioc.factory.FactoryPostProcessor
import com.aagu.ioc.util.PackageScanner
import com.aagu.ioc.util.StringUtils
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

class AdvisorManager: AdvisorRegistry, FactoryPostProcessor, PackageScanner.Filter {
    private val advisors = ArrayList<Advisor>()
    private val aspects = ArrayList<Class<*>>()
    private lateinit var factory: AbstractBeanFactory

    override fun registerAdvisor(ad: Advisor) {
        advisors.add(ad)
    }

    override fun getAdvisors(): ArrayList<Advisor> {
        return advisors
    }

    override fun process(beanDefinitionMap: ConcurrentHashMap<String, BeanDefinition>) {
        for (clazz in aspects) {
            val beanName = StringUtils.lowerCaseFirstChar(clazz.simpleName)
            for (method in clazz.methods) {
                processAdviceMethod(beanName, method)
            }
            val beanDef = GeneralBeanDefinition()
            beanDef.setBeanClass(clazz)
            factory.registerBeanDefinition(beanName, beanDef)
        }
    }

    override fun setBeanFactory(beanFactory: AbstractBeanFactory) {
        this.factory = beanFactory
    }

    override fun onFilter(clazz: Class<*>) {
        if (clazz.isAnnotationPresent(Aspect::class.java)) {
            aspects.add(clazz)
        }
    }

    /**
     * 处理切面定义，解析切面类型，设置切面优先级
     * 优先级0预留给系统切面（例如注解式事务），没有生命优先级的默认为1
     */
    private fun processAdviceMethod(beanName: String, method: Method) {
        var expression = ""
        var adviceType: Class<out Advice> = Advice::class.java
        when {
            method.isAnnotationPresent(Before::class.java) -> {
                expression = method.getAnnotation(Before::class.java).expression
                adviceType = BeforeAdvice::class.java
            }
            method.isAnnotationPresent(After::class.java) -> {
                expression = method.getAnnotation(After::class.java).expression
                adviceType = AfterAdvice::class.java
            }
            method.isAnnotationPresent(Around::class.java) -> {
                expression = method.getAnnotation(Around::class.java).expression
                adviceType = AroundAdvice::class.java
            }
            method.isAnnotationPresent(AfterThrow::class.java) -> {
                expression = method.getAnnotation(AfterThrow::class.java).expression
                adviceType = ExceptionAdvice::class.java
            }
        }
        if (expression.isNotEmpty()) {
            val order = if (method.isAnnotationPresent(Order::class.java)) {
                method.getAnnotation(Order::class.java).value
            } else {
                1
            }
            registerAdvisor(AspectJPointcutAdvisor(beanName, method.name, expression, adviceType, order))
        }
    }
}