package com.aagu.mvc

import java.io.IOException
import java.util.*
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class DispatcherServlet : HttpServlet() {
    private lateinit var handlerMapping: HandlerMapping
    private lateinit var handlerAdaptor: HandlerAdapter

    @Throws(ServletException::class)
    override fun init(config: ServletConfig) {

    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        this.doPost(req, resp)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        try {
            doDispatch(req, resp)
        } catch (ex: Exception) {
            resp.writer?.write(
                "500 Exception,Details:\r\n"
                        + Arrays.toString(ex.stackTrace).replace("[\\[\\]]".toRegex(), "")
                    .replace(",\\s".toRegex(), "\r\n")
            )
        }
    }

    @Throws(Exception::class)
    private fun doDispatch(req: HttpServletRequest, resp: HttpServletResponse) {
        val handler = handlerMapping.getHandler(req)

        handler?.let {
            resp.writer?.write("404 Not Found")
            return
        }

        val modelAndView = handlerAdaptor.handle(req, resp, handler!!)
        resp.writer.write(modelAndView?.model?.values.toString())
    }
}