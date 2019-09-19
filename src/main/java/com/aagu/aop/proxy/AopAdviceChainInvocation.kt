package com.aagu.aop.proxy

import com.aagu.aop.advice.AfterAdvice
import com.aagu.aop.advice.AroundAdvice
import com.aagu.aop.advice.BeforeAdvice
import java.lang.reflect.Method

class AopAdviceChainInvocation(
    private val proxy: Any,
    private val target: Any,
    private val method: Method,
    private val args: Array<Any>,
    private val advices: List<Any>
) {
    private var index = 0
    fun invoke(): Any? {
        if (index < advices.size) {
            when (val advice = advices[index++]) {
                is BeforeAdvice -> advice.before(method, args, target)
                is AroundAdvice -> return advice.around(invokeMethod, emptyArray(), this)
                is AfterAdvice -> {
                    var returnValue = this.invoke()
                    advice.after(returnValue, method, args, target)
                    return returnValue
                }
            }
            return this.invoke()
        } else {
            return method.invoke(target, *args)
        }
    }

    companion object {
        private lateinit var invokeMethod: Method

        init {
            try {
                invokeMethod = AopAdviceChainInvocation::class.java.getMethod("invoke")
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
        }
    }
}