package com.aagu.test

import com.aagu.ioc.TinyIocApplication
import com.aagu.ioc.annotation.Application
import com.aagu.ioc.runWithAnnotation
import com.aagu.ioc.runWithXml

@Application(property = "src/main/resources/app.properties")
class Demo: TinyIocApplication() {
    override fun run(args: Array<String>) {
        println(getBean<Bean1>("bean1"))
        println(getBean(Bean2::class.java))
        println(getBean(Bean3::class.java))
//        println(getBean<Bean4>("bean4"))
//        println(getBean<Bean4>("bean5"))
    }

}

fun main(args: Array<String>) {
    runWithAnnotation(Demo::class.java, args)
//    runWithXml(Demo::class.java, args)
}