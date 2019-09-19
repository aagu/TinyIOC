package com.aagu.aop.advisor

interface Advisor {
    fun getAdviceBeanName(): String
    fun getExpression(): String
}