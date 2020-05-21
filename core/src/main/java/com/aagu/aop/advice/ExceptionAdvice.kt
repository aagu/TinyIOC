package com.aagu.aop.advice

interface ExceptionAdvice: Advice {
    fun afterThrow(jointPoint: JointPoint)
}