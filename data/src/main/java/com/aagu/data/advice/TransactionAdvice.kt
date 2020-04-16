package com.aagu.data.advice

import com.aagu.aop.advice.ProceedJointPoint
import com.aagu.aop.annotation.Around
import com.aagu.aop.annotation.Aspect
import com.aagu.data.connection.DataSource
import com.aagu.data.transaction.TransactionManager
import com.aagu.data.transaction.support.SimpleTransactionDefinition
import com.aagu.ioc.bean.BeanFactoryAware
import com.aagu.ioc.factory.AbstractBeanFactory
import com.aagu.ioc.factory.BeanFactory
import java.lang.reflect.Method

@Aspect
class TransactionAdvice: BeanFactoryAware {
    private lateinit var factory: BeanFactory
    private lateinit var transactionManager: TransactionManager
    private lateinit var dataSource: DataSource

    @Around("@annotation(com.aagu.data.annotation.Transactional)")
    fun doTransaction(proceedJointPoint: ProceedJointPoint): Any? {
        if (!::transactionManager.isInitialized) {
            transactionManager = factory.getBean("transactionManager")
        }

        if (!::dataSource.isInitialized) {
            dataSource = factory.getBean("dataSource")
        }

        dataSource.setTransactional(true)

        val con = dataSource.getConnection()

        val transactionStatus = transactionManager.getTransaction(SimpleTransactionDefinition(con))

        val res: Any?
        try {
            res = proceedJointPoint.proceed()
        } catch (ex: Exception) {
            transactionManager.rollback(transactionStatus)
            dataSource.setTransactional(false)
            dataSource.freeConnection(con)
            return null
        }

        transactionManager.commit(transactionStatus)
        dataSource.setTransactional(false)
        dataSource.freeConnection(con)
        return res
    }

    override fun setBeanFactory(factory: AbstractBeanFactory) {
        this.factory = factory
    }

    companion object {
        val method: Method = TransactionAdvice::class.java.getMethod("doTransaction", ProceedJointPoint::class.java)
    }
}