package com.aagu.data.transaction

import com.aagu.tx.TransactionDefinition

class DataSourceTransactionDefinition() :
    TransactionDefinition {
    private var timeout: Int = -1

    override fun getName(): String {
        return "DataSourceTransactionDefinition"
    }

    override fun getTimeout(): Int {
        return timeout
    }

    fun setTimeout(second: Int) {
        this.timeout = second
    }
}