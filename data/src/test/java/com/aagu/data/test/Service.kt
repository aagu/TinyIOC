package com.aagu.data.test

import com.aagu.data.annotation.Transactional
import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Value

@Bean
@Transactional
open class Service(private val userDao: UserDao) {

    @Value("12") var num: Int = 0

    open fun doWork() {
        val users = userDao.getAll()

        for (user in users) {
            println(user)
        }
    }

    open fun modify() {
        userDao.add("xin", "wang")
        userDao.update("Tom", "Killer", 3)
    }
}