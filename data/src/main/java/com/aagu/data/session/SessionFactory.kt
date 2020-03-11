package com.aagu.data.session

import com.aagu.data.annotation.Delete
import com.aagu.data.annotation.Insert
import com.aagu.data.annotation.Select
import com.aagu.data.annotation.Update
import com.aagu.data.connection.ConnectPool
import com.aagu.data.proxy.Command
import com.aagu.data.proxy.RepositoryMethod
import com.aagu.data.proxy.RepositoryProxyFactory
import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.DestroyMethod
import com.aagu.ioc.annotation.InitMethod
import com.aagu.ioc.annotation.Value
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.Connection
import java.sql.ResultSet
import java.util.concurrent.LinkedBlockingQueue

@Bean
class SessionFactory private constructor(){
    private lateinit var pool : ConnectPool
    private val waitingQueue = LinkedBlockingQueue<String>()

    @Value("#{dataSourceUrl}")
    private lateinit var dataSourceUrl: String

    @Value("#{dataSourceDriver}")
    private lateinit var dataSourceDriver: String

    @Value("#{dataSourceUser")
    private var dataSourceUser: String? = null

    @Value("#{dataSourcePassword")
    private var dataSourcePassword: String? = null

    @InitMethod
    fun init() {
        pool = ConnectPool.getInstance(dataSourceDriver, dataSourceUrl, dataSourceUser, dataSourcePassword)
        pool.createPool()
    }

    fun query(sql: String): ResultSet {
        val conn: Connection = pool.getConnection()
        val stmt = conn.prepareStatement(sql)
        val res = stmt.executeQuery()
        pool.freeConnection(conn)
        return res
    }

    fun execute(sql: String): Int {
        val conn: Connection = pool.getConnection()
        val stmt = conn.prepareStatement(sql)
        val res = stmt.executeUpdate()
        pool.freeConnection(conn)
        return res
    }

    fun getRepository(repositoryInterface: Class<*>): Any {
        val repositoryProxyFactory = RepositoryProxyFactory(repositoryInterface)
        val repositoryProxy = repositoryProxyFactory.newInstance()

        parseCommand(repositoryProxyFactory, repositoryInterface)

        return repositoryProxy
    }

    private fun parseCommand(proxyFactory: RepositoryProxyFactory<out Any>, repositoryInterface: Class<*>) {
        val methods = repositoryInterface.declaredMethods
        for (method in methods) {
            var command: Command? = null
            when {
                method.isAnnotationPresent(Select::class.java) -> {
                    command = Command(Command.SELECT, method.getAnnotation(Select::class.java).sql)
                    command.entityClass = getEntityClass(method)
                }

                method.isAnnotationPresent(Insert::class.java) ->
                    command = Command(Command.INSERT, method.getAnnotation(Insert::class.java).sql)

                method.isAnnotationPresent(Update::class.java) ->
                    command = Command(Command.UPDATE, method.getAnnotation(Update::class.java).sql)

                method.isAnnotationPresent(Delete::class.java) ->
                    command = Command(Command.DELETE, method.getAnnotation(Delete::class.java).sql)
            }

            if (command != null) {
                proxyFactory.cacheMethod(method, RepositoryMethod(command, this))
            }
        }
    }

    private fun getEntityClass(method: Method): Class<*> {
        val returnType: Type = method.genericReturnType
        if (returnType is ParameterizedType) {
            val rawType = returnType.rawType
            if (rawType == List::class.java) {
                return returnType.actualTypeArguments[0] as Class<*>
            }
        } else if (returnType is Class<*>) {
            return returnType
        }

        return Void::class.java
    }


    @DestroyMethod
    fun close() {
        if (::pool.isInitialized) {
            pool.shutDownPool()
        }
    }
}