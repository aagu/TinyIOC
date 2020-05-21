package com.aagu.tx.support

import com.aagu.tx.TransactionDefinition
import com.aagu.tx.TransactionManager
import com.aagu.tx.TransactionStatus
import com.aagu.tx.exception.TransactionException
import com.aagu.tx.exception.UnexpectedRollbackException
import java.io.Serializable

abstract class AbstractTransactionManager : TransactionManager, Serializable {

    private var defaultTimeout = TransactionDefinition.TIMEOUT_DEFAULT

    private var rollbackOnCommitFailure = false


    fun setDefaultTimeout(defaultTimeout: Int) {
        if (defaultTimeout < TransactionDefinition.TIMEOUT_DEFAULT) {
            throw TransactionException("Invalid default timeout $defaultTimeout")
        }
        this.defaultTimeout = defaultTimeout
    }

    fun getDefaultTimeout(): Int {
        return defaultTimeout
    }

    fun setRollbackOnCommitFailure(rollbackOnCommitFailure: Boolean) {
        this.rollbackOnCommitFailure = rollbackOnCommitFailure
    }

    fun isRollbackOnCommitFailure(): Boolean {
        return rollbackOnCommitFailure
    }

    override fun getTransaction(definition: TransactionDefinition?): TransactionStatus {
        val def = definition ?: TransactionDefinition.withDefaults()

        val transaction: Any = doGetTransaction()

        return startTransaction(def, transaction)
    }

    override fun commit(status: TransactionStatus) {
        if (status.isCompleted()) {
            throw IllegalStateException("Transaction is already completed - do not call commit or rollback more than once per transaction")
        }

        val defStatus: DefaultTransactionStatus = status as DefaultTransactionStatus
        if (defStatus.isRollbackOnly()) {
            processRollback(defStatus, false)
            return
        }

        processCommit(defStatus)
    }

    private fun processCommit(status: DefaultTransactionStatus) {
        try {
            try {
                var unexpectedRollback = false
                prepareForCommit(status)

                when {
                    status.isNewTransaction() -> {
                        unexpectedRollback = status.isRollbackOnly()
                        doCommit(status)
                    }
                }

                // Throw UnexpectedRollbackException if we have a global rollback-only
                // marker but still didn't get a corresponding exception from commit.
                if (unexpectedRollback) {
                    throw UnexpectedRollbackException(
                        "Transaction silently rolled back because it has been marked as rollback-only"
                    )
                }
            } catch (ex: TransactionException) {
                // can only be caused by doCommit
                if (isRollbackOnCommitFailure()) {
                    doRollbackOnCommitException(status, ex)
                }
                throw ex
            } catch (ex: RuntimeException) {
                doRollbackOnCommitException(status, ex)
                throw ex
            } catch (ex: Error) {
                doRollbackOnCommitException(status, ex)
                throw ex
            }
        } finally {
            cleanupAfterCompletion(status)
        }
    }

    /**
     * Perform an actual commit of the given transaction.
     *
     * An implementation does not need to check the "new transaction" flag
     * or the rollback-only flag; this will already have been handled before.
     * Usually, a straight commit will be performed on the transaction object
     * contained in the passed-in status.
     * @param status the status representation of the transaction
     * @throws TransactionException in case of commit or system errors
     * @see DefaultTransactionStatus.getTransaction
     */
    @Throws(TransactionException::class)
    protected abstract fun doCommit(status: DefaultTransactionStatus)

    /**
     * Clean up after completion, clearing synchronization if necessary,
     * and invoking doCleanupAfterCompletion.
     * @param status object representing the transaction
     * @see .doCleanupAfterCompletion
     */
    private fun cleanupAfterCompletion(status: DefaultTransactionStatus) {
        status.setCompleted()
        if (status.isNewTransaction()) {
            doCleanupAfterCompletion(status.transaction)
        }
    }

    /**
     * Make preparations for commit, to be performed before the
     * `beforeCommit` synchronization callbacks occur.
     *
     * Note that exceptions will get propagated to the commit caller
     * and cause a rollback of the transaction.
     * @param status the status representation of the transaction
     * @throws RuntimeException in case of errors; will be **propagated to the caller**
     * (note: do not throw TransactionException subclasses here!)
     */
    protected open fun prepareForCommit(status: DefaultTransactionStatus?) {}

    /**
     * Perform an actual rollback of the given transaction.
     *
     * An implementation does not need to check the "new transaction" flag;
     * this will already have been handled before. Usually, a straight rollback
     * will be performed on the transaction object contained in the passed-in status.
     * @param status the status representation of the transaction
     * @throws TransactionException in case of system errors
     * @see DefaultTransactionStatus.getTransaction
     */
    @Throws(TransactionException::class)
    protected abstract fun doRollback(status: DefaultTransactionStatus)

    /**
     * Set the given transaction rollback-only. Only called on rollback
     * if the current transaction participates in an existing one.
     *
     * The default implementation throws an IllegalTransactionStateException,
     * assuming that participating in existing transactions is generally not
     * supported. Subclasses are of course encouraged to provide such support.
     * @param status the status representation of the transaction
     * @throws TransactionException in case of system errors
     */
    @Throws(TransactionException::class)
    protected open fun doSetRollbackOnly(status: DefaultTransactionStatus?) {
        throw TransactionException(
            "Participating in existing transactions is not supported - when 'isExistingTransaction' " +
                    "returns true, appropriate 'doSetRollbackOnly' behavior must be provided"
        )
    }

    override fun rollback(status: TransactionStatus) {
        if (status.isCompleted()) {
            throw IllegalStateException(
                "Transaction is already completed - do not call commit or rollback more than once per transaction"
            )
        }

        val defStatus = status as DefaultTransactionStatus
        processRollback(defStatus, false)
    }

    /**
     * Process an actual rollback.
     * The completed flag has already been checked.
     * @param status object representing the transaction
     * @throws TransactionException in case of rollback failure
     */
    private fun processRollback(status: DefaultTransactionStatus, unexpected: Boolean) {
        try {
            if (status.isNewTransaction()) {
                doRollback(status)
            } else {
                // Participating in larger transaction
                if (status.hasTransaction()) {
                    if (status.isRollbackOnly()) {
                        doSetRollbackOnly(status)
                    }
                }
            }

            // Raise UnexpectedRollbackException if we had a global rollback-only marker
            if (unexpected) {
                throw UnexpectedRollbackException(
                    "Transaction rolled back because it has been marked as rollback-only"
                )
            }
        } finally {
            cleanupAfterCompletion(status)
        }
    }

    /**
     * Invoke `doRollback`, handling rollback exceptions properly.
     * @param status object representing the transaction
     * @param ex the thrown application exception or error
     * @throws TransactionException in case of rollback failure
     * @see .doRollback
     */
    @Throws(TransactionException::class)
    private fun doRollbackOnCommitException(status: DefaultTransactionStatus, ex: Throwable) {
        if (status.isNewTransaction()) {
            doRollback(status)
        } else if (status.hasTransaction()) {
            doSetRollbackOnly(status)
        }
    }

    /**
     * Cleanup resources after transaction completion.
     *
     * Called after `doCommit` and `doRollback` execution,
     * on any outcome. The default implementation does nothing.
     *
     * Should not throw any exceptions but just issue warnings on errors.
     * @param transaction the transaction object returned by `doGetTransaction`
     */
    protected open fun doCleanupAfterCompletion(transaction: Any?) {}

    @Throws(TransactionException::class)
    protected abstract fun doGetTransaction(): Any

    @Throws(TransactionException::class)
    protected abstract fun doBegin(transaction: Any, definition: TransactionDefinition)

    /**
     * Start a new transaction.
     */
    private fun startTransaction(definition: TransactionDefinition, transaction: Any): TransactionStatus {
        val status = newTransactionStatus(definition, transaction)
        doBegin(transaction, definition)
        return status
    }

    /**
     * Create a TransactionStatus instance for the given arguments.
     */
    protected open fun newTransactionStatus(
        definition: TransactionDefinition, transaction: Any?
    ): DefaultTransactionStatus {
        return DefaultTransactionStatus(
            transaction, definition.isReadOnly()
        )
    }
}