package com.aagu.mvc

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.context.ApplicationContext
import com.aagu.mvc.adapter.HandlerAdapter
import com.aagu.mvc.adapter.HandlerMethodAdapter
import com.aagu.mvc.mapping.HandlerMapping
import com.aagu.mvc.mapping.HandlerMethodMapping
import com.aagu.mvc.util.Serializer
import java.io.IOException
import java.util.*
import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Bean
class DispatcherServlet : HttpServlet() {
    private lateinit var handlerMapping: HandlerMapping
    private lateinit var handlerAdaptor: HandlerAdapter

    @Throws(ServletException::class)
    override fun init(config: ServletConfig) {

    }

    fun registerMapping(context: ApplicationContext) {
        handlerMapping = HandlerMethodMapping(context)
        handlerAdaptor = HandlerMethodAdapter()
    }

    public override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        this.doPost(req, resp)
    }

    @Throws(ServletException::class, IOException::class)
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        try {
            doDispatch(req, resp)
        } catch (ex: Exception) {
            val error = "500 Exception,Details:\r\n ${Arrays.toString(ex.stackTrace).replace("[\\[\\]]".toRegex(), "")
                .replace(",\\s".toRegex(), "\r\n")}"
            resp.setContentLength(error.length)
            resp.writer?.write(error)
            ex.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun doDispatch(req: HttpServletRequest, resp: HttpServletResponse) {
        val handler = handlerMapping.getHandler(req)

        if (handler == null) {
            resp.status = 404
            val msg = "unknown mapping ${req.requestURI}"
            resp.setContentLength(msg.length)
            resp.writer.write(msg)
        } else {
            resp.status = 200
            val responseBody = if (handler.isResponseBody()) {
                resp.contentType = "text/json"
                Serializer.getSerializedString(handlerAdaptor.handleRest(req, resp, handler))
            } else {
                val modelAndView = handlerAdaptor.handleMV(req, resp, handler)
                if (modelAndView != null) {
                    modelAndView.model?.values.toString()
                } else {
                    "empty"
                }
            }
            resp.setContentLength(responseBody.length)
            resp.writer.write(responseBody)
        }
    }
}