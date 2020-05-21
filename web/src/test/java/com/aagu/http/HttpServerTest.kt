package com.aagu.http

import com.aagu.ioc.annotation.Application
import com.aagu.ioc.runWithAnnotation

@Application(property = "app-web.properties")
class HttpServerTest : TinyWebApplication()

fun main() {
    runWithAnnotation(HttpServerTest::class.java, emptyArray())
}