package com.aagu.data.transaction.support

import com.aagu.data.exception.TransactionException
import com.aagu.data.transaction.TransactionDefinition
import com.aagu.data.transaction.TransactionManager
import com.aagu.data.transaction.TransactionStatus
import com.aagu.ioc.annotation.Bean
import java.sql.SQLException

@Bean("transactionManager")
class SimpleTransactionManager : TransactionManager {

    override fun getTransaction(definition: TransactionDefinition?): TransactionStatus {
        val status = DefaultTransactionStatus(definition!!.getConnect(), true, null, false)
        status.connection.autoCommit = false

        return status
    }

    override fun commit(status: TransactionStatus) {
        val con = (status as DefaultTransactionStatus).connection
        try {
            con.commit()
        } catch (ex: SQLException) {
            throw TransactionException("Could not commit JDBC transaction")
        }
    }

    override fun rollback(status: TransactionStatus) {
        val con = (status as DefaultTransactionStatus).connection
        try {
            con.rollback()
        } catch (ex: SQLException) {
            throw TransactionException("Could not rollback JDBC transaction")
        }
    }
}