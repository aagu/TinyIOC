package com.aagu.flow

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Context(private val req: HttpServletRequest, private val resp: HttpServletResponse) {
    fun string(code: Int, string: String) {
        resp.contentType = "text/plain"
        byte(code, string.toByteArray())
    }

    private fun byte(code: Int, data: ByteArray) {
        resp.status = code
        resp.setContentLength(data.size)
        resp.writer.write(String(data))
    }

    fun html(code: Int, html: String) {
        resp.contentType = "text/html"
        byte(code, html.toByteArray())
    }
}