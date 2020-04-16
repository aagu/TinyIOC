package com.aagu.data.transaction

import com.aagu.data.exception.TransactionException

interface TransactionManager {
    @Throws(TransactionException::class)
    fun getTransaction(definition: TransactionDefinition?): TransactionStatus

    @Throws(TransactionException::class)
    fun commit(status: TransactionStatus)

    @Throws(TransactionException::class)
    fun rollback(status: TransactionStatus)
}