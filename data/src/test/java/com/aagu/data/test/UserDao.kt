package com.aagu.data.test

import com.aagu.data.annotation.Param
import com.aagu.data.annotation.Repository
import com.aagu.data.annotation.Select

@Repository
interface UserDao {
    @Select("select id, first_name, last_name from user where id=\$id")
    fun getAll(name: String, @Param("id") id: Int): List<User>
}