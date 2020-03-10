package com.aagu.aop.advice

interface BeforeAdvice: Advice {
    fun before(jointPoint: JointPoint)
}