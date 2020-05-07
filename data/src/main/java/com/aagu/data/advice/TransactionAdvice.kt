package com.aagu.data.advice

import com.aagu.aop.advice.ProceedJointPoint
import com.aagu.aop.annotation.Around
import com.aagu.aop.annotation.Aspect
import com.aagu.aop.annotation.Order
import com.aagu.data.transaction.DataSourceTransactionDefinition
import com.aagu.data.util.TransactionUtils
import com.aagu.ioc.bean.BeanFactoryAware
import com.aagu.ioc.factory.AbstractBeanFactory
import com.aagu.ioc.factory.BeanFactory
import com.aagu.tx.TransactionManager
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@Aspect
class TransactionAdvice: BeanFactoryAware {
    private lateinit var factory: BeanFactory
    private lateinit var transactionManager: TransactionManager

    @Around("@annotation(com.aagu.data.annotation.Transactional)")
    @Order(0)
    fun doTransaction(proceedJointPoint: ProceedJointPoint): Any? {
        if (!::transactionManager.isInitialized) {
            transactionManager = factory.getBean("transactionManager")
        }

        val transactionStatus = transactionManager.getTransaction(
            DataSourceTransactionDefinition()
        )

        val res: Any?
        try {
            res = proceedJointPoint.proceed()
        } catch (ex: Exception) {
            if (ex is InvocationTargetException) {
                ex.targetException.printStackTrace()
            } else {
                ex.printStackTrace()
            }
            transactionManager.rollback(transactionStatus)
            return null
        }

        if (isRollbackOnly(proceedJointPoint)) {
            transactionManager.rollback(transactionStatus)
        } else {
            transactionManager.commit(transactionStatus)
        }
        return res
    }

    override fun setBeanFactory(factory: AbstractBeanFactory) {
        this.factory = factory
    }

    companion object {
        val method: Method = TransactionAdvice::class.java.getMethod("doTransaction", ProceedJointPoint::class.java)

        fun isRollbackOnly(proceedJointPoint: ProceedJointPoint): Boolean {
            return TransactionUtils.isRollbackOnly(proceedJointPoint.method)
        }
    }
}