package com.aagu.mvc

import com.aagu.mvc.method.HandlerMethod
import javax.servlet.http.HttpServletRequest

interface HandlerMapping {
    @Throws(Exception::class)
    fun getHandler(req: HttpServletRequest): HandlerMethod?
}