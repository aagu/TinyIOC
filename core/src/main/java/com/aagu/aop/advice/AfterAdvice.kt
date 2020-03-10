package com.aagu.aop.advice

interface AfterAdvice: Advice {
    fun after(jointPoint: JointPoint)
}