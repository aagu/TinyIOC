package com.aagu.data.transaction.support

import com.aagu.data.transaction.TransactionDefinition
import java.sql.Connection

class SimpleTransactionDefinition(private val connection: Connection) :
    TransactionDefinition {
    private var timeout: Int = -1

    override fun getPropagationBehavior(): Int {
        return TransactionDefinition.PROPAGATION_SUPPORTS
    }

    override fun getIsolationLevel(): Int {
        return connection.transactionIsolation
    }

    fun setIsolationLevel(isolationLevel: Int) {
        connection.transactionIsolation = isolationLevel
    }

    override fun getName(): String {
        return "SimpleTransactionManager"
    }

    override fun getTimeout(): Int {
        return timeout
    }

    fun setTimeout(second: Int) {
        this.timeout = second
    }

    override fun isReadOnly(): Boolean {
        return connection.isReadOnly
    }

    fun setReadOnly(readOnly: Boolean) {
        connection.isReadOnly = readOnly
    }

    override fun getConnect(): Connection {
        return connection
    }
}