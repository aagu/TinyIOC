package com.aagu.data.session

import com.aagu.data.annotation.Repository
import com.aagu.ioc.bean.BeanDefinition
import com.aagu.ioc.bean.FactoryBeanDefinition
import com.aagu.ioc.factory.FactoryPostProcessor
import com.aagu.ioc.util.PackageScanner
import java.util.concurrent.ConcurrentHashMap

class SessionManager : FactoryPostProcessor, PackageScanner.Filter {
    private val daoClasses = HashSet<Class<*>>()

    override fun process(beanDefinitionMap: ConcurrentHashMap<String, BeanDefinition>) {
        for (clazz in daoClasses) {
            val beanName = clazz.simpleName
            val beanDefinition = FactoryBeanDefinition()
            beanDefinition.setFactoryBeanName("SessionFactory")
            beanDefinition.setFactoryMethodName("getRepository")
            beanDefinition.setConstructorArguments(arrayOf(clazz))
            beanDefinition.allowGenericType = true
            beanDefinition.setTargetClass(clazz)
            beanDefinitionMap[beanName] = beanDefinition
        }
    }

    override fun onFilter(clazz: Class<*>) {
        if (clazz.isAnnotationPresent(Repository::class.java)) {
            daoClasses.add(clazz)
        }
    }
}