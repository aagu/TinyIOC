package com.aagu.ioc.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bean(val beanName: String = "", val scope: String = "singleton")