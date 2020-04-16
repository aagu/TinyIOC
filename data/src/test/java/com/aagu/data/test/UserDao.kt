package com.aagu.data.test

import com.aagu.data.annotation.*

@Repository
interface UserDao {
    @Select("select id, first_name, last_name from user where id=\$id")
    fun getById(@Param("id") id: Int): List<User>

    @Select("select id, first_name, last_name from user")
    fun getAll(): List<User>

    @Update("update user set first_name='\$firstName', last_name='\$lastName' where id=\$id")
    fun update(@Param("firstName") firstName: String,
               @Param("lastName") lastName: String, id: Int): Int

    @Insert("insert into user(first_name, last_name) values('\$firstName', '\$lastName')")
    fun add(@Param("firstName") firstName: String,
            @Param("lastName") lastName: String)
}