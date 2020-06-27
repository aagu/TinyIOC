package com.aagu.flow

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Wire
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Bean("httpServlet")
class DispatcherServlet : HttpServlet() {
    @Wire private lateinit var engine: Engine

    override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
        doDispatch(req, resp)
    }

    private fun doDispatch(req: HttpServletRequest, resp: HttpServletResponse) {
        val key = "${req.method}-${req.requestURI}"
        val handlerFunc = engine.getRoute(key)
        if (handlerFunc != null) {
            handlerFunc.handle(Context(req, resp))
        } else {
            resp.writer.write("404 Not Found: ${req.requestURI}\n")
        }
    }
}