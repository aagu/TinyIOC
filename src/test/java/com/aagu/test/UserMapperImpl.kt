package com.aagu.test

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.DestroyMethod
import com.aagu.ioc.annotation.InitMethod
import com.aagu.mapper.Processor
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

@Bean
class UserMapperImpl : UserMapper {
    private lateinit var conn: Connection

    @InitMethod
    fun init() {
        try {
            val url = "jdbc:sqlite:/data/JavaLearning/IOC/testdb.db"
            conn = DriverManager.getConnection(url)
        } catch (ex: SQLException) {
            println(ex.message)
        }
    }

    override fun getAll(): List<User> {
        val sql = "SELECT id, first_name, last_name from user"
        try {
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery(sql)
            return Processor.createEntityList(User::class.java, rs)
        } catch (ex: SQLException) {
            print(ex.message)
        }
        return emptyList()
    }

    @DestroyMethod
    fun close() {
        if (::conn.isInitialized) {
            try {
                conn.close()
            } catch (ex: SQLException) {
                print(ex.message)
            }
        }
    }
}