package com.aagu.aop.advisor

import com.aagu.aop.advice.Advice

interface Advisor {
    fun getAdviceType(): Class<out Advice>
    fun getAdviceBeanName(): String
    fun getAdviceMethodName(): String
    fun getExpression(): String
    fun getOrder(): Int
}