package com.aagu.test

import com.aagu.aop.advice.JointPoint
import com.aagu.aop.advice.ProceedJointPoint
import com.aagu.aop.annotation.AfterThrow
import com.aagu.aop.annotation.Around
import com.aagu.aop.annotation.Aspect
import com.aagu.aop.annotation.Before

@Aspect
class MethodAdvice {
    @AfterThrow("execution (* com.aagu.test.Bean1.init())")
    fun afterInitThrow(jointPoint: JointPoint) {
        println("error in executing ${jointPoint.target.javaClass.name}")
    }

    @Before("execution (* com.aagu.test.Bean1.doSomething())")
    fun beforeDo(jointPoint: JointPoint) {
        println("before method call")
    }

    @Around("execution (* com.aagu.test.Bean1.doAnotherThing()))")
    fun aroundBean(proceedJointPoint: ProceedJointPoint) {
        println("prepare method call")
        proceedJointPoint.proceed()
        println("method called")
    }
}