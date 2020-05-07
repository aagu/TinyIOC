package com.aagu.data.session

import com.aagu.data.annotation.Delete
import com.aagu.data.annotation.Insert
import com.aagu.data.annotation.Select
import com.aagu.data.annotation.Update
import com.aagu.data.connection.DataSource
import com.aagu.data.proxy.Command
import com.aagu.data.proxy.RepositoryMethod
import com.aagu.data.proxy.RepositoryProxyFactory
import com.aagu.data.util.SqlUtils
import com.aagu.data.util.TransactionUtils
import com.aagu.ioc.annotation.Bean
import com.aagu.ioc.annotation.Wire
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.Connection
import java.sql.ResultSet

@Bean
class SessionFactory private constructor(){

    @Wire
    private lateinit var dataSource: DataSource

    fun query(sql: String, args: Map<String, Any>): ResultSet {
        val conn: Connection = dataSource.getConnection()
        val parsedSql = SqlUtils.prepareSql(sql, args)
        val stmt = conn.prepareStatement(parsedSql)
        val res = stmt.executeQuery()
        dataSource.freeConnection(conn)
        return res
    }

    fun execute(sql: String, args: Map<String, Any>): Int {
        val conn: Connection = dataSource.getConnection()
        val parsedSql = SqlUtils.prepareSql(sql, args)
        val stmt = conn.prepareStatement(parsedSql)
        val res = stmt.executeUpdate()
        dataSource.freeConnection(conn)
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
                command.readOnly = TransactionUtils.isReadOnly(method)
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
}