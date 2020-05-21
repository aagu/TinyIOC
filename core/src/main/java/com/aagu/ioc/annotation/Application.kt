package com.aagu.ioc.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Application(val basePackage: String = "", val xmlLocation: String = "", val property: String = "")