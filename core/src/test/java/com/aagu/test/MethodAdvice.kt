package com.aagu.test

import com.aagu.aop.advice.JointPoint
import com.aagu.aop.annotation.Aspect
import com.aagu.aop.annotation.Before

@Aspect
class MethodAdvice {
    @Before("execution (* com.aagu.test.Bean1.doSomething())")
    fun before(jointPoint: JointPoint) {
        println("before method call")
    }
}