package com.aagu.data.util

import com.aagu.data.annotation.Transactional
import java.lang.reflect.Method

object TransactionUtils {
    fun isReadOnly(method: Method): Boolean {
        return getTransactionAnnotation(method)?.readOnly ?: false
    }

    fun isRollbackOnly(method: Method): Boolean {
        return getTransactionAnnotation(method)?.rollbackOnly ?: false
    }

    private fun getTransactionAnnotation(method: Method): Transactional? {
        return when {
            method.isAnnotationPresent(Transactional::class.java) -> {
                method.getAnnotation(Transactional::class.java)
            }
            method.declaringClass.isAnnotationPresent(Transactional::class.java) -> {
                method.declaringClass.getAnnotation(Transactional::class.java)
            }
            else -> {
                null
            }
        }
    }
}