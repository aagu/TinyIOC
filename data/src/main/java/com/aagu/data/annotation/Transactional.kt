package com.aagu.data.annotation

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Transactional(
    val readOnly: Boolean = false,

    val rollbackOnly: Boolean = false
)