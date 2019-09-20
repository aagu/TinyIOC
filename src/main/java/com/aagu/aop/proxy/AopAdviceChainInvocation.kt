package com.aagu.aop.proxy

import com.aagu.aop.advice.*
import java.lang.reflect.Method

class AopAdviceChainInvocation(
    private val proxy: Any,
    private val target: Any,
    private val method: Method,
    private val args: Array<Any>,
    private val advices: List<AdviceDelegate>
) {
    private var index = 0
    fun invoke(): Any? {
        if (index < advices.size) {
            val advice = advices[index++]
            when (advice.adviceType) {
                BeforeAdvice::class.java -> (advice.getDelegate() as BeforeAdvice).before(method, args, target)
                AroundAdvice::class.java -> return (advice.getDelegate() as AroundAdvice).around(invokeMethod, emptyArray(), this)
                AfterAdvice::class.java -> {
                    var returnValue = this.invoke()
                    (advice.getDelegate() as AfterAdvice).after(returnValue, method, args, target)
                    return returnValue
                }
                ExceptionAdvice::class.java -> {
                    return try {
                        method.invoke(target, *args)
                    } catch (e: Exception) {
                        (advice.getDelegate() as ExceptionAdvice).afterThrow(e, method, args, target)
                        null
                    }
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