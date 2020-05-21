package com.aagu.data.transaction

import com.aagu.tx.support.DefaultTransactionStatus

class DataSourceTransactionStatus(transactionObj: DataSourceTransactionObject, readOnly: Boolean) :
    DefaultTransactionStatus(transactionObj, readOnly) {
}