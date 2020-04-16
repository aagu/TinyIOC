package com.aagu.data.connection

import java.sql.Connection

interface DataSource {
    fun getConnection(): Connection

    fun freeConnection(connection: Connection)

    fun setTransactional(transactional: Boolean)

    fun isTransactional(): Boolean
}