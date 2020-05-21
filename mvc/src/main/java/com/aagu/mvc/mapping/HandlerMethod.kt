package com.aagu.mvc.mapping

import com.aagu.mvc.annotation.RequestMapping
import java.lang.reflect.Method

data class HandlerMethod(val bean: Any, val method: Method, var isBody: Boolean) {

    constructor(bean: Any, method: Method): this(bean, method, false)

    fun checkHandleType(type: String): Boolean {
        for (typ in method.getAnnotation(RequestMapping::class.java).type) {
            if (typ.toString() == type) return true
        }
        return false
    }

    fun setResponseBody(asResponseBody: Boolean) {
        isBody = asResponseBody
    }

    fun isResponseBody(): Boolean {
        return isBody
    }
}