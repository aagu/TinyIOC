package com.aagu.aop.advice

class AdviceWrapper(val beanName: String, private val methodName: String, val target: Any, val adviceType: Class<*>):
    BeforeAdvice, AfterAdvice, AroundAdvice, ExceptionAdvice
{
    override fun before(jointPoint: JointPoint) {
        val adviceMethod = target::class.java.getMethod(methodName, JointPoint::class.java)
        adviceMethod.invoke(target, jointPoint)
    }

    override fun after(jointPoint: JointPoint) {
        val adviceMethod = target::class.java.getMethod(methodName, JointPoint::class.java)
        adviceMethod.invoke(target, jointPoint)
    }

    override fun afterThrow(jointPoint: JointPoint) {
        val adviceMethod = target::class.java.getMethod(methodName, JointPoint::class.java)
        adviceMethod.invoke(target, jointPoint)
    }

    override fun around(proceedJointPoint: ProceedJointPoint) {
        val adviceMethod = target::class.java.getMethod(methodName, ProceedJointPoint::class.java)
        adviceMethod.invoke(target, proceedJointPoint)
    }
}