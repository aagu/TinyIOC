package com.aagu.aop.advice

class AdviceWrapper(val beanName: String, val target: Any, val adviceType: Class<*>):
    BeforeAdvice, AfterAdvice, AroundAdvice, ExceptionAdvice
{
    override fun before(jointPoint: JointPoint) {
        val adviceMethod = target::class.java.getMethod("before", JointPoint::class.java)
        adviceMethod.invoke(target, jointPoint)
    }

    override fun after(jointPoint: JointPoint) {
        val adviceMethod = target::class.java.getMethod("after", JointPoint::class.java)
        adviceMethod.invoke(target, jointPoint)
    }

    override fun afterThrow(jointPoint: JointPoint) {
        val adviceMethod = target::class.java.getMethod("afterThrow", JointPoint::class.java)
        adviceMethod.invoke(target, jointPoint)
    }

    override fun around(proceedJointPoint: ProceedJointPoint) {
        val adviceMethod = target::class.java.getMethod("around", ProceedJointPoint::class.java)
        adviceMethod.invoke(target, proceedJointPoint)
    }
}