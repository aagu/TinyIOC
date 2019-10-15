package com.aagu.aop.advice

import java.lang.reflect.Method

class ProceedJointPoint(method: Method, target: Any, args: Array<Any?>) : JointPoint(method, target, args) {
    fun proceed(): Any? {
        return method.invoke(target, *args)
    }
}