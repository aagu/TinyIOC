package com.aagu.tx

import com.aagu.tx.exception.TransactionException

interface TransactionManager {
    @Throws(TransactionException::class)
    fun getTransaction(definition: TransactionDefinition?): TransactionStatus

    @Throws(TransactionException::class)
    fun commit(status: TransactionStatus)

    @Throws(TransactionException::class)
    fun rollback(status: TransactionStatus)
}