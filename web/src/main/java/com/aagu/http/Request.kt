package com.aagu.http

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.security.Principal
import java.util.*
import javax.servlet.*
import javax.servlet.http.*
import kotlin.collections.HashMap

class Request(
    private val ins: InputStream,
    private val serverPort: Int,
    private val localPort: Int
) : HttpServletRequest {
    private var url: String = ""
    private var method: String = "GET"
    private var ver: String = "HTTP/1.0"
    private val headers = HashMap<String, String>()
    private val requestParameters = HashMap<String, Array<String>>()

    fun parse() {
        val request = StringBuffer(Response.BUFFER_SIZE)
        val buffer = ByteArray(Response.BUFFER_SIZE)

        val len: Int
        len = try {
            ins.read(buffer)
        } catch (ex: IOException) {
            ex.printStackTrace()
            -1
        }
        for (j in 0 until len) {
            request.append(buffer[j].toChar())
        }

        parseRequest(request.toString())
    }

    @Throws(IllegalArgumentException::class)
    private fun parseRequestLine(requestString: String) {
        val strings = requestString.split(" ")
        if (strings.size != 3) {
            throw IllegalStateException("invalid request header")
        }
        method = strings[0]
        parseUrl(strings[1])
        ver = strings[2]
    }

    private fun parseUrl(string: String) {
        val strings = string.split("?")
        url = strings[0]
        if (strings.size > 1) {
            val parameterStrings = string.split("?")[1].split("&")
            for (str in parameterStrings) {
                val pair = str.split("=")
                requestParameters[pair[0]] = pair[1].split(",").toTypedArray()
            }
        }
    }

    private fun parseRequestHeaders(lines: List<String>): Int {
        var currentLine = 1;
        for (idx in 1 until lines.size) {
            val strings = lines[idx].split(" ")
            if (strings.size == 2) {
                headers[strings[0]] = strings[1]
                currentLine = idx
            }
        }
        return currentLine
    }

    private fun parseRequestBody(lines: List<String>) {
        //TODO not implemented
    }

    private fun parseRequest(requestString: String) {
        val lines = requestString.split("\r\n")
        parseRequestLine(lines[0])
        val headerEndLine = parseRequestHeaders(lines)
        if (headerEndLine < lines.size - 1 && method == "POST") {
            parseRequestBody(lines.subList(headerEndLine, lines.size))
        }
    }

    override fun isUserInRole(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun startAsync(): AsyncContext {
        TODO("Not yet implemented")
    }

    override fun startAsync(p0: ServletRequest?, p1: ServletResponse?): AsyncContext {
        TODO("Not yet implemented")
    }

    override fun getPathInfo(): String {
        TODO("Not yet implemented")
    }

    override fun getProtocol(): String {
        TODO("Not yet implemented")
    }

    override fun getCookies(): Array<Cookie> {
        TODO("Not yet implemented")
    }

    override fun getParameterMap(): MutableMap<String, Array<String>> {
        return requestParameters
    }

    override fun getRequestURL(): StringBuffer {
        TODO("Not yet implemented")
    }

    override fun getAttributeNames(): Enumeration<String> {
        return Collections.enumeration(requestParameters.keys)
    }

    override fun setCharacterEncoding(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getParameterValues(p0: String?): Array<String> {
        TODO("Not yet implemented")
    }

    override fun getRemoteAddr(): String {
        TODO("Not yet implemented")
    }

    override fun isAsyncStarted(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getContentLengthLong(): Long {
        TODO("Not yet implemented")
    }

    override fun getLocales(): Enumeration<Locale> {
        TODO("Not yet implemented")
    }

    override fun getRealPath(p0: String?): String {
        TODO("Not yet implemented")
    }

    override fun login(p0: String?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun getContextPath(): String {
        return ""
    }

    override fun isRequestedSessionIdValid(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getServerPort(): Int {
        return serverPort
    }

    override fun getAttribute(p0: String?): Any {
        TODO("Not yet implemented")
    }

    override fun getDateHeader(p0: String?): Long {
        TODO("Not yet implemented")
    }

    override fun getRemoteHost(): String {
        TODO("Not yet implemented")
    }

    override fun getRequestedSessionId(): String {
        TODO("Not yet implemented")
    }

    override fun getServletPath(): String {
        TODO("Not yet implemented")
    }

    override fun getSession(p0: Boolean): HttpSession {
        TODO("Not yet implemented")
    }

    override fun getSession(): HttpSession {
        TODO("Not yet implemented")
    }

    override fun getServerName(): String {
        TODO("Not yet implemented")
    }

    override fun getLocalAddr(): String {
        TODO("Not yet implemented")
    }

    override fun isSecure(): Boolean {
        TODO("Not yet implemented")
    }

    override fun <T : HttpUpgradeHandler?> upgrade(p0: Class<T>?): T {
        TODO("Not yet implemented")
    }

    override fun isRequestedSessionIdFromCookie(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getPart(p0: String?): Part {
        TODO("Not yet implemented")
    }

    override fun getRemoteUser(): String {
        TODO("Not yet implemented")
    }

    override fun getLocale(): Locale {
        TODO("Not yet implemented")
    }

    override fun getMethod(): String {
        return method
    }

    override fun isRequestedSessionIdFromURL(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLocalPort(): Int {
        return localPort
    }

    override fun isRequestedSessionIdFromUrl(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getServletContext(): ServletContext {
        TODO("Not yet implemented")
    }

    override fun getQueryString(): String {
        TODO("Not yet implemented")
    }

    override fun getDispatcherType(): DispatcherType {
        TODO("Not yet implemented")
    }

    override fun getHeaders(p0: String?): Enumeration<String> {
        TODO("Not yet implemented")
    }

    override fun getUserPrincipal(): Principal {
        TODO("Not yet implemented")
    }

    override fun getParts(): MutableCollection<Part> {
        TODO("Not yet implemented")
    }

    override fun getReader(): BufferedReader {
        TODO("Not yet implemented")
    }

    override fun getScheme(): String {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override fun getInputStream(): ServletInputStream {
        TODO("Not yet implemented")
    }

    override fun getLocalName(): String {
        TODO("Not yet implemented")
    }

    override fun isAsyncSupported(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAuthType(): String {
        TODO("Not yet implemented")
    }

    override fun getCharacterEncoding(): String {
        TODO("Not yet implemented")
    }

    override fun getParameterNames(): Enumeration<String> {
        TODO("Not yet implemented")
    }

    override fun authenticate(p0: HttpServletResponse?): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAttribute(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun getPathTranslated(): String {
        TODO("Not yet implemented")
    }

    override fun getContentLength(): Int {
        TODO("Not yet implemented")
    }

    override fun getHeader(p0: String?): String? {
        return headers[p0]
    }

    override fun getIntHeader(p0: String?): Int {
        val value = headers[p0]
        if (value != null && value != "") {
            return value.toInt()
        } else {
            throw NumberFormatException()
        }
    }

    override fun changeSessionId(): String {
        TODO("Not yet implemented")
    }

    override fun getContentType(): String {
        TODO("Not yet implemented")
    }

    override fun getAsyncContext(): AsyncContext {
        TODO("Not yet implemented")
    }

    override fun getRequestURI(): String {
        return url
    }

    override fun getRequestDispatcher(p0: String?): RequestDispatcher {
        TODO("Not yet implemented")
    }

    override fun getHeaderNames(): Enumeration<String> {
        return Collections.enumeration(headers.keys)
    }

    override fun setAttribute(p0: String?, p1: Any?) {
        TODO("Not yet implemented")
    }

    override fun getParameter(p0: String?): String {
        TODO("Not yet implemented")
    }

    override fun getRemotePort(): Int {
        TODO("Not yet implemented")
    }
}