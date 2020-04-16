package com.aagu.data.transaction

interface TransactionStatus {
    fun isNewTransaction(): Boolean
    fun hasSavepoint(): Boolean
    fun setRollbackOnly()
    fun isRollbackOnly(): Boolean
    fun isCompleted(): Boolean
}