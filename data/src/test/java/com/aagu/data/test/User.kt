package com.aagu.data.test

import com.aagu.data.annotation.Entity

@Entity
class User {
    var id: Int = 0
    lateinit var firstName: String
    lateinit var lastName: String

    override fun toString(): String {
        return "User(id=$id, firstName='$firstName', lastName='$lastName')"
    }
}