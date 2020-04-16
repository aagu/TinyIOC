package com.aagu.data.transaction.support

import com.aagu.data.transaction.TransactionStatus
import java.sql.Connection

class DefaultTransactionStatus(
    val connection: Connection, val newSynchronization: Boolean,
    val transaction: Any?, val readOnly: Boolean
) : TransactionStatus {
    private var rollbackOnly = false
    private var isNew = true
    private var completed = false

    override fun isNewTransaction(): Boolean {
        return isNew
    }

    fun isNewSynchronization(): Boolean {
        return this.newSynchronization
    }

    fun setNotNew() {
        isNew = false
    }

    override fun hasSavepoint(): Boolean {
        return false
    }

    override fun setRollbackOnly() {
        rollbackOnly = true
    }

    override fun isRollbackOnly(): Boolean {
        return rollbackOnly
    }

    override fun isCompleted(): Boolean {
        return completed
    }

    fun setCompleted() {
        completed = true
    }

    fun hasTransaction(): Boolean {
        return this.transaction != null
    }
}