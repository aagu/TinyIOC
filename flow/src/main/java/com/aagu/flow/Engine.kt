package com.aagu.flow

import com.aagu.ioc.annotation.Bean

@Bean
class Engine {

    interface HandlerFunc {
        fun handle(context: Context)
    }

    private val router = HashMap<String, HandlerFunc>()

    private fun addRoute(method: String, pattern: String, handlerFunc: HandlerFunc) {
        val key = "$method-$pattern"
        router[key] = handlerFunc
    }

    fun get(pattern: String, handlerFunc: HandlerFunc) {
        addRoute("GET", pattern, handlerFunc)
    }

    fun post(pattern: String, handlerFunc: HandlerFunc) {
        addRoute("POST", pattern, handlerFunc)
    }

    fun getRoute(key: String) : HandlerFunc? {
        return router[key]
    }
}