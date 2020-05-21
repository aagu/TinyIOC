package com.aagu.data.transaction

import java.sql.Connection

class DataSourceTransactionObject {
    private lateinit var connection: Connection
    private var restoreAutoCommit = false

    fun hasConnection(): Boolean {
        return ::connection.isInitialized
    }

    fun setConnection(newConnection: Connection) {
        connection = newConnection
    }

    fun getConnection(): Connection {
        return connection
    }

    fun setMustRestoreAutoCommit(autoCommit: Boolean) {
        restoreAutoCommit = autoCommit
    }

    fun mustRestoreAutoCommit(): Boolean {
        return restoreAutoCommit
    }
}