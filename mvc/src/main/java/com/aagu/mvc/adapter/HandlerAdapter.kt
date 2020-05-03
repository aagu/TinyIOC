package com.aagu.mvc.adapter

import com.aagu.mvc.mapping.HandlerMethod
import com.aagu.mvc.view.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface HandlerAdapter {
    @Throws(Exception::class)
    fun handleMV(req: HttpServletRequest, resp: HttpServletResponse, handler: HandlerMethod): ModelAndView?
    @Throws(Exception::class)
    fun handleRest(req: HttpServletRequest, resp: HttpServletResponse, handler: HandlerMethod): Any?
}