package com.aagu.data.transaction

import java.sql.Connection

object StaticTransactionDefinition : TransactionDefinition {
    private lateinit var connection: Connection

    override fun getConnect(): Connection {
        return connection
    }

    fun setConnection(connection: Connection) {
        this.connection = connection
    }
}