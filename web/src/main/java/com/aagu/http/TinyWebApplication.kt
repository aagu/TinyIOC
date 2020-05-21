package com.aagu.http

import com.aagu.ioc.TinyIocApplication

open class TinyWebApplication : TinyIocApplication() {
    override fun run(args: Array<String>) {
        val httpServer = getBean(HttpServer::class.java)
        httpServer.run()
    }
}