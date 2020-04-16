package com.aagu.data.test

import com.aagu.aop.advice.ProceedJointPoint
import com.aagu.aop.annotation.Around

//@Aspect
class MethodAdvice {
    @Around("execution (* com.aagu.data.test.Service.doWork())")
    fun around(proceedJointPoint: ProceedJointPoint): Any? {
        println("before method exec")
        val res = proceedJointPoint.proceed()
        println("after method exec")
        return res
    }
}