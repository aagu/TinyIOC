package com.aagu.aop.advisor

import com.aagu.aop.pointcut.Pointcut

interface PointcutAdvisor: Advisor {
    fun getPointcut(): Pointcut
}