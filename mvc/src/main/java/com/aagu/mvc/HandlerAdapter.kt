package com.aagu.mvc

import com.aagu.mvc.method.HandlerMethod
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface HandlerAdapter {
    @Throws(Exception::class)
    fun handle(req: HttpServletRequest, resp: HttpServletResponse, handler: HandlerMethod): ModelAndView?
}