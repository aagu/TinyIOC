package com.aagu.ioc.annotation

import java.lang.annotation.RetentionPolicy

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bean(val beanName: String = "")