package com.aagu.aop.advice

interface AroundAdvice: Advice {
    fun around(proceedJointPoint: ProceedJointPoint): Any?
}