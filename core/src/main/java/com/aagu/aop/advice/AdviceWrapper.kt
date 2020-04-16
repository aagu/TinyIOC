package com.aagu.aop.advice

import com.aagu.aop.advisor.Advisor

class AdviceWrapper(private val advisor: Advisor, private val target: Any):
    BeforeAdvice, AfterAdvice, AroundAdvice, ExceptionAdvice, Comparable<AdviceWrapper>
{
    private val beanName: String
    private val methodName: String
    val adviceType: Class<out Advice>

    init {
        val type = advisor.getAdviceType()
        if (!adviceTypes.contains(type)) throw RuntimeException("无法创建Advice包装对象，未知Advice类型!")
        else adviceType = type
        beanName = advisor.getAdviceBeanName()
        methodName = advisor.getAdviceMethodName()
    }

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

    override fun around(proceedJointPoint: ProceedJointPoint): Any? {
        val adviceMethod = target::class.java.getMethod(methodName, ProceedJointPoint::class.java)
        return adviceMethod.invoke(target, proceedJointPoint)
    }

    override fun compareTo(other: AdviceWrapper): Int {
        return advisor.getOrder() - other.advisor.getOrder()
    }

    companion object {
        private val adviceTypes = HashSet<Class<out Advice>>()

        init {
            adviceTypes.add(AfterAdvice::class.java)
            adviceTypes.add(AroundAdvice::class.java)
            adviceTypes.add(BeforeAdvice::class.java)
            adviceTypes.add(ExceptionAdvice::class.java)
        }
    }
}