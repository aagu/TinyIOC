package com.aagu.data.proxy

import com.aagu.data.session.SessionFactory
import com.aagu.data.util.Processor
import java.lang.reflect.AnnotatedType

class RepositoryMethod(private val command: Command, private val sessionFactory: SessionFactory) {
    var argParameters: Array<AnnotatedType>? = null

    fun execute(args: Array<out Any>?): Any {
        val argsMap = createArgsMap(args)
        return if (command.type == Command.SELECT) {
            val result = sessionFactory.query(command.sql, argsMap)
            Processor.bindList(command.entityClass!!, result)
        } else {
            sessionFactory.execute(command.sql, argsMap)
        }
    }

    private fun createArgsMap(args: Array<out Any>?): Map<String, Any> {
        val map = HashMap<String, Any>()
//        argParameters?.let {
//            for (idx in it.indices) {
//                val name = it[idx].
//                map[name] = args!![idx]
//            }
//        }
        args?.let {
            for ((idx, arg) in it.withIndex()) {
                map["var$idx"] = args[idx]
            }
        }

        return map
    }
}