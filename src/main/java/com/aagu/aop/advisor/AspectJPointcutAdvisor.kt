package com.aagu.aop.advisor

import com.aagu.aop.pointcut.AspectJExpressionPointcut
import com.aagu.aop.pointcut.Pointcut

class AspectJPointcutAdvisor(adviceBeanName: String, expression: String): AbstractPointcutAdvisor(adviceBeanName, expression) {
    private val pointcut: Pointcut

    init {
        pointcut = AspectJExpressionPointcut(expression)
    }

    override fun getPointcut(): Pointcut {
        return pointcut
    }
}