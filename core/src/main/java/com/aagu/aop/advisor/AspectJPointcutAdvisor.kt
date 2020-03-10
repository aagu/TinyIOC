package com.aagu.aop.advisor

import com.aagu.aop.pointcut.AspectJExpressionPointcut
import com.aagu.aop.pointcut.Pointcut

class AspectJPointcutAdvisor(
    adviceBeanName: String,
    adviceMethodName: String,
    expression: String,
    adviceType: Class<*>
): AbstractPointcutAdvisor(adviceBeanName, adviceMethodName, expression, adviceType) {
    private val pointcut: Pointcut

    init {
        pointcut = AspectJExpressionPointcut(expression)
    }

    override fun getPointcut(): Pointcut {
        return pointcut
    }
}