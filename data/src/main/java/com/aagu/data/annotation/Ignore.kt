package com.aagu.data.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Ignore(val value: Boolean = false)