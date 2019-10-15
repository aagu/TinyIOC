package com.aagu.test

import com.aagu.aop.advice.*
import com.aagu.aop.annotation.AfterThrow
import com.aagu.aop.annotation.Around
import com.aagu.aop.annotation.Before
import com.aagu.ioc.annotation.Bean

@Bean
class MethodAdvice: BeforeAdvice, ExceptionAdvice, AroundAdvice {

    @AfterThrow("execution (* com.aagu.test.Bean1.init())")
    override fun afterThrow(jointPoint: JointPoint) {
        println("error in executing ${jointPoint.target.javaClass.name}")
    }

    @Before("execution (* com.aagu.test.Bean1.doSomething())")
    override fun before(jointPoint: JointPoint) {
        println("before method call")
    }

    @Around("execution (* com.aagu.test.Bean1.doAnotherThing()))")
    override fun around(proceedJointPoint: ProceedJointPoint) {
        println("prepare method call")
        proceedJointPoint.proceed()
        println("method called")
    }
}