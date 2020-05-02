package com.aagu.data.connection

import java.sql.Connection

interface DataSource {
    fun getConnection(): Connection

    fun freeConnection(connection: Connection)

    fun setTransactional(transactionalState: Boolean)

    fun isTransactional(): Boolean
}