package com.aagu.test

import com.aagu.ioc.TinyIocApplication
import com.aagu.ioc.annotation.Application
import com.aagu.ioc.runWithAnnotation

@Application(property = "app-no-data.properties")
class Demo : TinyIocApplication() {
    override fun run(args: Array<String>) {
        val gear = getBean(Gear::class.java)

        gear.getBean1()
//        println(gear.toString())
//        println(gear.whoAmI())
    }
}

fun main(args: Array<String>) {
    runWithAnnotation(Demo::class.java, args)
}