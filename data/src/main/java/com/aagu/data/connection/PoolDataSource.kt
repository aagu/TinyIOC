package com.aagu.data.connection

import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.DestroyMethod
import com.aagu.ioc.annotation.InitMethod
import com.aagu.ioc.annotation.Value
import java.sql.Connection

@Bean("dataSource")
class PoolDataSource: DataSource {
    private lateinit var pool : ConnectPool
    private val transactional: ThreadLocal<Boolean> = ThreadLocal()

    private val transactionalConnection = ThreadLocal<Connection?>()

    @Value("#{dataSourceUrl}")
    private lateinit var dataSourceUrl: String

    @Value("#{dataSourceDriver}")
    private lateinit var dataSourceDriver: String

    @Value("#{dataSourceUser}:null")
    private var dataSourceUser: String? = null

    @Value("#{dataSourcePassword}:null")
    private var dataSourcePassword: String? = null
    
    @Value("#{dataSourceTimeOut}:5000L")
    private var dataSourceTimeout: Long = 1000L

    @Value("#{dataSourceInitConnections}:10")
    private var initConnections: Int = 10

    @Value("#{dataSourceIncConnections}:5")
    private var incConnections: Int = 5

    @Value("#{dataSourceMaxConnections}:50")
    private var maxConnections = 50

    @InitMethod
    fun init() {
        pool = ConnectPool.getInstance(dataSourceDriver, dataSourceUrl, dataSourceUser, dataSourcePassword)
        pool.timeout = dataSourceTimeout
        pool.initialConnections = initConnections
        pool.incrementConnections = incConnections
        pool.maxConnections = maxConnections
        pool.createPool()
    }

    override fun setTransactional(transactionalState: Boolean) {
        transactional.set(transactionalState)
    }

    override fun isTransactional(): Boolean {
        val v = transactional.get()
        if (v != null) return v
        return false
    }

    override fun getConnection(): Connection {
        return if (isTransactional()) {
            if (transactionalConnection.get() == null) {
                transactionalConnection.set(pool.getConnection())
            }

            transactionalConnection.get()!!
        } else {
            pool.getConnection()
        }
    }

    override fun freeConnection(connection: Connection) {
        if (!isTransactional()) {
            val transactionalCon = transactionalConnection.get()
            if (transactionalCon != null) {
                pool.freeConnection(transactionalCon)
            }
            pool.freeConnection(connection)
        }
    }

    @DestroyMethod
    fun close() {
        if (::pool.isInitialized) {
            pool.shutDownPool()
        }
    }
}