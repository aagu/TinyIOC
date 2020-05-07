package com.aagu.tx

interface TransactionStatus {
    fun isNewTransaction(): Boolean
    fun setRollbackOnly()
    fun isRollbackOnly(): Boolean
    fun isCompleted(): Boolean

    /**
     * Mark this transaction as completed or rolled back
     */
    fun setCompleted()
}