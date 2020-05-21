package com.aagu.tx.support

import com.aagu.tx.TransactionStatus

open class DefaultTransactionStatus(
    val transaction: Any?, val readOnly: Boolean
) : TransactionStatus {
    private var rollbackOnly = false
    private var isNew = true
    private var completed = false

    override fun isNewTransaction(): Boolean {
        return isNew
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

    override fun setCompleted() {
        completed = true
    }

    fun hasTransaction(): Boolean {
        return this.transaction != null
    }
}