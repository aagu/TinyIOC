package com.aagu.aop.advisor

import com.aagu.aop.pointcut.Pointcut
import com.aagu.aop.pointcut.SimplePointcut

class LogAdvisor(adviceBeanName: String, expression: String) : AbstractPointcutAdvisor(adviceBeanName, expression) {
    override fun getPointcut(): Pointcut {
        return SimplePointcut()
    }
}