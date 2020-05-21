package com.aagu.data.proxy

import com.aagu.data.session.SessionFactory
import com.aagu.data.sql.Variable
import com.aagu.data.util.Processor

class RepositoryMethod(private val command: Command, private val sessionFactory: SessionFactory) {
    lateinit var argVariables: Array<Variable>

    fun execute(args: Array<out Any>?): Any? {
        val argsMap = createArgsMap(args)
        return if (command.type == Command.SELECT) {
            val result = sessionFactory.query(command.sql, argsMap)
            Processor.bindList(command.entityClass!!, result)
        } else if (!command.readOnly) {
            sessionFactory.execute(command.sql, argsMap)
        } else null
    }

    private fun createArgsMap(args: Array<out Any>?): Map<String, Any> {
        val map = HashMap<String, Any>()

        argVariables.let {
            for (idx in it.indices) {
                map[it[idx].name!!] = args!![idx]
            }
        }

        return map
    }
}