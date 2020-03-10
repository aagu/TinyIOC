package com.aagu.aop.advisor

interface Advisor {
    fun getAdviceType(): Class<*>
    fun getAdviceBeanName(): String
    fun getAdviceMethodName(): String
    fun getExpression(): String
}