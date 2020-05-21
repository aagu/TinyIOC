package com.aagu.aop.advisor

import com.aagu.aop.advice.Advice
import com.aagu.aop.pointcut.Pointcut

abstract class AbstractPointcutAdvisor(
    private val adviceBeanName: String,
    private val adviceMethodName: String,
    private val expression: String,
    private val adviceType: Class<out Advice>,
    private val order: Int
): PointcutAdvisor {
    override fun getAdviceType(): Class<out Advice> {
        return adviceType
    }

    override fun getAdviceBeanName(): String {
        return adviceBeanName
    }

    override fun getExpression():String {
        return expression
    }

    override fun getAdviceMethodName(): String {
        return adviceMethodName
    }

    override fun getOrder(): Int {
        return order
    }

    abstract override fun getPointcut(): Pointcut
}