package com.aagu.data.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Wire

@Bean
class Service {
    @Wire lateinit var userDao: UserDao

    fun doWork() {
        val users = userDao.getAll()

        for (user in users) {
            println(user)
        }
    }
}