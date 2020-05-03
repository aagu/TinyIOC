package com.aagu.mvc.mapping

import javax.servlet.http.HttpServletRequest

interface HandlerMapping {
    @Throws(Exception::class)
    fun getHandler(req: HttpServletRequest): HandlerMethod?
}