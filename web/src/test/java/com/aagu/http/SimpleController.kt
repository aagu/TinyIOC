package com.aagu.http

import com.aagu.flow.Context
import com.aagu.flow.Engine
import com.aagu.ioc.annotation.Bean
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

//@Controller
@Bean
class SimpleController(engine: Engine) {

    init {
        engine.get("/", object : Engine.HandlerFunc {
            override fun handle(context: Context) {
                context.string(200, "Flow Web Framework!\n")
            }
        })

        engine.get("/hello", object : Engine.HandlerFunc {
            override fun handle(context: Context) {
                context.html(200, "<h1>Hello Flow!</h1>\n")
            }
        })
    }

//    @RequestMapping("/")
//    fun index(): String {
//        return "Hello World"
//    }

//    @RequestMapping("/weather")
//    fun weatherInfo(): String {
//        return "sunny"
//    }

//    @RequestMapping("/info/:name/id")
//    fun wildcard(@PathVar("name") name: String): String {
//        return "hello $name"
//    }
}