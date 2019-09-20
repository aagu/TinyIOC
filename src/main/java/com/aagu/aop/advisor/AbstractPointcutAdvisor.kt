package com.aagu.aop.advisor

import com.aagu.aop.pointcut.Pointcut

abstract class AbstractPointcutAdvisor(
    private val adviceBeanName: String,
    private val expression: String,
    private val adviceType: Class<*>
): PointcutAdvisor {
    override fun getAdviceType(): Class<*> {
        return adviceType
    }

    override fun getAdviceBeanName(): String {
        return adviceBeanName
    }

    override fun getExpression():String {
        return expression
    }

    abstract override fun getPointcut(): Pointcut
}