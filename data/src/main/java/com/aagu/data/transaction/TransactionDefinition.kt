package com.aagu.data.transaction

import java.sql.Connection

interface TransactionDefinition {
    fun getPropagationBehavior(): Int {
        return PROPAGATION_REQUIRED
    }

    fun getIsolationLevel(): Int {
        return ISOLATION_DEFAULT
    }

    fun getName(): String? {
        return null
    }

    fun getTimeout(): Int {
        return TIMEOUT_DEFAULT
    }

    fun isReadOnly(): Boolean {
        return false
    }

    fun getConnect(): Connection

    companion object {
        const val ISOLATION_DEFAULT = -1
        const val ISOLATION_READ_UNCOMMITTED = 1
        const val ISOLATION_READ_COMMITTED = 2
        const val ISOLATION_REPEATABLE_READ = 4
        const val ISOLATION_SERIALIZABLE = 8

        const val PROPAGATION_REQUIRED = 0
        const val PROPAGATION_SUPPORTS = 1
        const val PROPAGATION_MANDATORY = 2
        const val PROPAGATION_REQUIRES_NEW = 3
        const val PROPAGATION_NOT_SUPPORTED = 4
        const val PROPAGATION_NEVER = 5
        const val PROPAGATION_NESTED = 6

        const val TIMEOUT_DEFAULT = -1

        fun withDefaults(): TransactionDefinition {
            return StaticTransactionDefinition
        }
    }
}