package com.aagu.data.connection

import com.aagu.data.exception.PoolExhaustedException
import com.aagu.data.exception.PoolNotInitializedException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException
import java.util.concurrent.CopyOnWriteArrayList

class ConnectPool private constructor(
    private val jdbcDriver: String,
    private val dbUrl: String,
    private val dbUserName: String?,
    private val dbPassword: String?
) {
    private var testStatement: String = ""
    var initialConnections = 10
    var incrementConnections = 5
    var maxConnections = 50
    var timeout = 1000L
    private var pooledConnections: CopyOnWriteArrayList<PooledConnection>? = null

    fun createPool() {
        if (pooledConnections != null) return

        println("creating pool...")
        val driver = Class.forName(jdbcDriver).newInstance() as Driver
        DriverManager.registerDriver(driver)
        pooledConnections = CopyOnWriteArrayList()
        createConnections(initialConnections)
        println("pool created!")
    }

    private fun createConnections(initialConnections: Int) {
        for (i in 0 until initialConnections) {
            if (maxConnections > 0 && pooledConnections!!.size > maxConnections) break

            try {
                pooledConnections?.add(PooledConnection(newConnection()))
            } catch (ex: SQLException) {
                println("error in creating connection")
                throw ex
            }
        }
    }

    private fun newConnection(): Connection {
        val conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword)

        // 首次连接，检查数据库允许的最大连接数
        if (pooledConnections!!.size == 0) {
            val metaDta = conn.metaData
            val driverMaxConnection = metaDta.maxConnections

            if (driverMaxConnection in 1 until maxConnections) maxConnections = driverMaxConnection
        }

        return conn
    }

    @Throws(SQLException::class, PoolNotInitializedException::class)
    fun getConnection(): Connection = runBlocking {
        if (pooledConnections == null) throw PoolNotInitializedException()

        var conn = getFreeConnection()

        val result = withTimeoutOrNull(timeout) {
            while (conn == null) {
                delay(250)

                conn = getFreeConnection()
            }
            conn
        }
        result?: throw PoolExhaustedException()
    }

    private fun getFreeConnection(): Connection? {
        var conn = findFreeConnection()

        if (conn == null) {
            createConnections(incrementConnections)

            conn = findFreeConnection()
        }

        return conn
    }

    private fun findFreeConnection(): Connection? {
        var conn: Connection? = null

        for (connection in pooledConnections!!) {
            if (!connection.busy) {
                conn = connection.connection
                connection.busy = true

                if (!testConnection(conn)) {
                    try {
                        conn = newConnection()
                        connection.connection = conn
                    } catch (ex: SQLException) {
                        println("error in creating connection ${ex.message}")
                    }
                }

                break
            }
        }
        return conn
    }

    private fun testConnection(conn: Connection?): Boolean {
        if (conn == null) return false
        try {
            if (testStatement == "") {
                conn.autoCommit = true
            } else {
                conn.createStatement().execute(testStatement)
            }
        } catch (ex: SQLException) {
            closeConnection(conn)
            return false
        }
        return true
    }

    fun freeConnection(conn: Connection) {
        if (pooledConnections == null) {
            println("connection pool not exist, could not free connection")
            return
        }

        for (pooledConnection in pooledConnections!!) {
            if (pooledConnection.connection == conn) {
                pooledConnection.busy = false
                break
            }
        }
    }

    @Throws(SQLException::class)
    @Synchronized
    fun refreshConnection() {
        if (pooledConnections == null) {
            println("connection pool not exist, could not refresh connections")
            return
        }

        for (connection in pooledConnections!!) {
            if (connection.busy) {
                runBlocking {
                    delay(5000)
                }

                closeConnection(connection.connection!!)
                connection.connection = newConnection()
                connection.busy = false
            }
        }
    }

    @Throws(SQLException::class)
    @Synchronized
    fun shutDownPool() {
        if (pooledConnections == null) {
            println("connection pool not exist, could not shut down")
            return
        }

        println("shutting down...")
        while (pooledConnections!!.size > 0) {
            val connection = pooledConnections!!.first()
            if (connection.busy) {
                runBlocking { delay(5000) }
                closeConnection(connection.connection!!)
            }
            pooledConnections!!.remove(connection)
        }

        pooledConnections = null
        println("shut down")
    }

    private fun closeConnection(conn: Connection) {
        try {
            conn.close()
        } catch (ex: SQLException) {
            println("error in close connection ${ex.message}")
        }
    }

    companion object {
        @Volatile
        private var instance: ConnectPool? = null

        fun getInstance(
            jdbcDriver: String,
            dbUrl: String,
            dbUserName: String?,
            dbPassword: String?
        ): ConnectPool {
            if (instance == null) {
                synchronized(ConnectPool::class.java) {
                    if (instance == null) {
                        instance = ConnectPool(jdbcDriver, dbUrl, dbUserName, dbPassword)
                    }
                }
            }

            return instance!!
        }
    }

    class PooledConnection(var connection: Connection? = null) {
        var busy = false
    }
}