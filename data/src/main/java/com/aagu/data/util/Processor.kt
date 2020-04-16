package com.aagu.data.util

import com.aagu.data.annotation.Column
import com.aagu.data.annotation.Entity
import com.aagu.data.annotation.Ignore
import com.aagu.data.exception.NotEntityException
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.sql.ResultSet

object Processor {
    fun <T> bindList(entityClass: Class<T>, rs: ResultSet): List<T> {
        return when (entityClass) {
            String::class.java -> {
                bindStringList(rs)
            }
            Int::class.java -> {
                bindIntList(rs)
            }
            else -> {
                bindEntityList(entityClass, rs)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> bindStringList(rs: ResultSet): List<T> {
        val list = ArrayList<T>()

        while (rs.next()) {
            list.add(rs.getString(1) as T)
        }

        return list
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> bindIntList(rs: ResultSet): List<T> {
        val list = ArrayList<T>()

        while (rs.next()) {
            list.add(rs.getInt(1) as T)
        }

        return list
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> bindEntityList(entityClass: Class<T>, rs: ResultSet): List<T> {
        if (!entityClass.isAnnotationPresent(Entity::class.java)) throw NotEntityException(entityClass.simpleName)
        val fields = entityClass.declaredFields
        val entities = ArrayList<T>()
        val constructor = chooseConstructor(entityClass)

        while (rs.next()) {
            // 首先构造实例

            if (constructor.parameterCount > 0) throw InstantiationError("constructor parameter count > 0")

            val entity = constructor.newInstance()

            // 之后注入值
            for (field in fields) {
                if (field.isAnnotationPresent(Ignore::class.java)) continue

                field.isAccessible = true

                val columnName = columnNameBuilder(field)

                when (field.type) {
                    Int::class.java -> field.set(entity, rs.getInt(columnName))
                    String::class.java -> field.set(entity, rs.getString(columnName))
                    Long::class.java -> field.set(entity, rs.getLong(columnName))
                }
            }

            entities.add(entity as T)
        }

        return entities
    }

    private fun columnNameBuilder(field: Field): String {
        return if (field.isAnnotationPresent(Column::class.java)) {
            field.getAnnotation(Column::class.java).name
        } else {
            val origName: String = field.name
            val builder = StringBuilder()
            for (char in origName.toCharArray()) {
                if (char.isUpperCase()) {
                    builder.append("_").append(char.toLowerCase())
                } else {
                    builder.append(char)
                }
            }

            builder.toString()
        }
    }

    private fun chooseConstructor(clazz: Class<*>): Constructor<*> {
        var constructor = clazz.constructors[0]

        for (cons in clazz.constructors) {
            // 优先选择无参构方法
            if (cons.parameterCount == 0) {
                constructor = cons
                break
            }
        }

        return constructor
    }
}