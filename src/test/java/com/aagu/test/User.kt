package com.aagu.test

import com.aagu.mapper.annotation.Column
import com.aagu.mapper.annotation.Entity

@Entity("user")
class User {
    @Column("id") var id: Int? = null
    @Column("first_name") var firstName: String? = null
    @Column("last_name") var lastName: String? = null

    override fun toString(): String {
        return "User(id=$id, firstName=$firstName, lastName=$lastName)"
    }
}