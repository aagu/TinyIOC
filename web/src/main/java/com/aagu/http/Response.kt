package com.aagu.http

import java.io.IOException
import java.io.OutputStream
import java.io.PrintWriter
import java.util.*
import javax.servlet.ServletOutputStream
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import kotlin.collections.HashMap

class Response(private val out: OutputStream) : HttpServletResponse {
    private lateinit var writer: PrintWriter
    private var ver = "HTTP/1.1"
    private var statusCode = 200
    private var statusString = ""
    private var contentLength: Long = 0L
    private var contentType = "text/html"
    private val headers = HashMap<String, String>()

    companion object {
        const val BUFFER_SIZE = 2048
    }

    override fun encodeURL(p0: String?): String {
        TODO("Not yet implemented")
    }

    override fun encodeUrl(p0: String?): String {
        TODO("Not yet implemented")
    }

    override fun addIntHeader(p0: String, p1: Int) {
        headers[p0] = p1.toString()
    }

    override fun addCookie(p0: Cookie?) {
        TODO("Not yet implemented")
    }

    override fun encodeRedirectUrl(p0: String?): String {
        TODO("Not yet implemented")
    }

    override fun flushBuffer() {
        writer.flush()
    }

    override fun encodeRedirectURL(p0: String?): String {
        TODO("Not yet implemented")
    }

    override fun sendRedirect(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun setBufferSize(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun getLocale(): Locale {
        TODO("Not yet implemented")
    }

    override fun sendError(p0: Int, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun sendError(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun setContentLengthLong(p0: Long) {
        contentLength = p0
    }

    override fun setCharacterEncoding(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun addDateHeader(p0: String, p1: Long) {
        headers[p0] = Date(p1).toString()
    }

    override fun setLocale(p0: Locale?) {
        TODO("Not yet implemented")
    }

    override fun getHeaders(p0: String?): MutableCollection<String> {
        TODO("Not yet implemented")
    }

    override fun addHeader(p0: String, p1: String) {
        headers[p0] = p1
    }

    override fun setContentLength(p0: Int) {
        contentLength = p0.toLong()
    }

    override fun getBufferSize(): Int {
        TODO("Not yet implemented")
    }

    override fun resetBuffer() {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    override fun setDateHeader(p0: String?, p1: Long) {
        TODO("Not yet implemented")
    }

    override fun getStatus(): Int {
        return statusCode
    }

    override fun getCharacterEncoding(): String {
        TODO("Not yet implemented")
    }

    override fun isCommitted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setStatus(p0: Int) {
        statusCode = p0
    }

    override fun setStatus(p0: Int, p1: String) {
        statusCode = p0
        statusString = p1
    }

    override fun getHeader(p0: String): String {
        val value =  headers[p0]
        if (value != null) return value
        throw IllegalStateException("can not get header $p0")
    }

    override fun getContentType(): String {
        return contentType
    }

    override fun getWriter(): PrintWriter {
        if (::writer.isInitialized) throw IOException("writer is already gotten")
        writer = PrintWriter(out)
        if (statusString.isNotEmpty()) {
            writer.write("$ver $statusCode $statusString\r\n")
        } else {
            writer.write("$ver $statusCode\r\n")
        }
        writer.write("Date: ${Date()}\r\n")
        writer.write("Content-Type: $contentType\r\n")
        writer.write("Content-Length: $contentLength\r\n")
        writer.write("\r\n")
        return writer
    }

    override fun containsHeader(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun setIntHeader(p0: String?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun getHeaderNames(): MutableCollection<String> {
        TODO("Not yet implemented")
    }

    override fun setHeader(p0: String, p1: String) {
        headers[p0] = p1
    }

    override fun getOutputStream(): ServletOutputStream {
        TODO("Not yet implemented")
    }

    override fun setContentType(p0: String) {
        contentType = p0
    }
}