package com.aagu.aop.advice

import java.lang.reflect.Method

open class JointPoint(protected val method: Method, val target: Any, val args: Array<Any?>) {
    protected var returnValue:Any? = null
    protected var exception: Exception? = null

    constructor(returnValue: Any?, method: Method, target: Any, args: Array<Any?>): this(method, target, args) {
        this.returnValue = returnValue
    }

    constructor(exception: Exception?, method: Method, target: Any, args: Array<Any?>): this(method, target, args) {
        this.exception = exception
    }
}