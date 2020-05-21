package com.aagu.data.test

import com.aagu.ioc.TinyIocApplication
import com.aagu.ioc.annotation.Application
import com.aagu.ioc.runWithAnnotation

@Application(property = "app.properties")
class Demo : TinyIocApplication() {
    override fun run(args: Array<String>) {
        println("Hello Test")

//        val sessionFactory = getBean(SessionFactory::class.java)
//
//        val res = sessionFactory.query("SELECT id, first_name, last_name from user")

        val service = getBean(Service::class.java)
        println("--query--")
        service.doWork()
        println("--modify--")
        service.modify()
        println("userDao.add(\"xin\", \"wang\")")
        println("--query again--")
        service.doWork()

//        val thisClazz = this::class.java
//        for (method in thisClazz.methods) {
//            if (thisClazz == method.declaringClass) println(method)
//        }
    }

    fun <T> reflect(clazz: Class<T>): T {
        return clazz.newInstance() as T
    }
}

fun main() {
    runWithAnnotation(Demo::class.java, emptyArray())
}