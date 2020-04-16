package com.aagu.test

import com.aagu.aop.advice.JointPoint
import com.aagu.aop.advice.ProceedJointPoint
import com.aagu.aop.annotation.*

@Aspect
class MethodAdvice {
    @Before("execution (* com.aagu.test.Gear.*(..))")
    @Order(2)
    fun before(jointPoint: JointPoint) {
        println("before method call")
    }

    @After("execution (* com.aagu.test.Gear.getBean1())")
    @Order(1)
    fun after(jointPoint: JointPoint) {
        println("after bean1 got")
    }

    @Around("execution (* com.aagu.test.Gear.getBean1())")
    fun around(proceedJointPoint: ProceedJointPoint): Any? {
        println("around start")
        val res = proceedJointPoint.proceed()
        println("around end")
        return res
    }
}