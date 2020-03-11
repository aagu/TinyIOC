package com.aagu.data.proxy

import com.aagu.data.session.SessionFactory
import com.aagu.data.util.Processor

class RepositoryMethod(private val command: Command, private val sessionFactory: SessionFactory) {
    fun execute(args: Array<out Any>?): Any {
        return if (command.type == Command.SELECT) {
            val result = sessionFactory.query(command.sql)
            return Processor.bindList(command.entityClass!!, result)
        } else {
            sessionFactory.execute(command.sql)
        }
    }
}