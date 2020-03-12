package com.aagu.test

import com.aagu.ioc.TinyIocApplication
import com.aagu.ioc.annotation.Application
import com.aagu.ioc.runWithAnnotation

@Application(property = "app.properties")
class Demo : TinyIocApplication() {
    override fun run(args: Array<String>) {
        val bean1 = getBean(Bean1::class.java)

        bean1.doSomething()

        println(getBean<Gear>("gear"))
    }
}

fun main() {
    runWithAnnotation(Demo::class.java, emptyArray())
}