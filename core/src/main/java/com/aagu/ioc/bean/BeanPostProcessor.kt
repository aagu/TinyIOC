package com.aagu.ioc.bean

interface BeanPostProcessor : Comparable<BeanPostProcessor>{
    fun postProcessBeforeInitialization(beanName: String, bean: Any): Any {
        return bean
    }

    fun postProcessAfterInitialization(beanName: String, bean: Any): Any {
        return bean
    }

    fun getPriority(): Int {
        return 0; //unset
    }

    override fun compareTo(other: BeanPostProcessor): Int {
        return this.getPriority() - other.getPriority();
    }
}