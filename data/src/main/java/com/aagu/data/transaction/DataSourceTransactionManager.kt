package com.aagu.data.transaction

import com.aagu.data.connection.DataSource
import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Wire
import com.aagu.tx.TransactionDefinition
import com.aagu.tx.exception.TransactionException
import com.aagu.tx.support.AbstractTransactionManager
import com.aagu.tx.support.DefaultTransactionStatus
import java.sql.SQLException

@Bean("transactionManager")
class DataSourceTransactionManager : AbstractTransactionManager() {

    @Wire private lateinit var dataSource: DataSource

    override fun doCommit(status: DefaultTransactionStatus) {
        val txObj = status.transaction as DataSourceTransactionObject
        val conn = txObj.getConnection()
        try {
            conn.commit()
        } catch (ex: SQLException) {
            throw TransactionException("Could not commit JDBC transaction: ${ex.message}")
        }
    }

    override fun doRollback(status: DefaultTransactionStatus) {
        val txObj = status.transaction as DataSourceTransactionObject
        val conn = txObj.getConnection()
        try {
            conn.rollback()
        } catch (ex: SQLException) {
            throw TransactionException("Could not rollback JDBC transaction: ${ex.message}")
        }
    }

    override fun doGetTransaction(): Any {
        return DataSourceTransactionObject()
    }

    override fun doBegin(transaction: Any, definition: TransactionDefinition) {
        val txObj = transaction as DataSourceTransactionObject

        try {
            if (!txObj.hasConnection()) {
                dataSource.setTransactional(true)
                val con = dataSource.getConnection()

                txObj.setConnection(con)
                if (con.autoCommit) {
                    txObj.setMustRestoreAutoCommit(true)
                    con.autoCommit = false
                }
            }
        } catch (ex: Throwable) {
            if (txObj.hasConnection()) {
                dataSource.setTransactional(false)
                if (txObj.mustRestoreAutoCommit()) {
                    txObj.getConnection().autoCommit = true
                }
                dataSource.freeConnection(txObj.getConnection())
            }
        }
    }

    override fun doCleanupAfterCompletion(transaction: Any?) {
        super.doCleanupAfterCompletion(transaction)
        val txObj = transaction as DataSourceTransactionObject
        dataSource.setTransactional(false)
        if (txObj.mustRestoreAutoCommit()) {
            txObj.getConnection().autoCommit = true
        }
        dataSource.freeConnection(txObj.getConnection())
    }
}