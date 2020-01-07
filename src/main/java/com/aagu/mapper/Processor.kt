package com.aagu.mapper

import com.aagu.mapper.annotation.Column
import com.aagu.mapper.annotation.Entity
import com.aagu.mapper.annotation.Ignore
import com.aagu.mapper.exception.NotEntityException
import java.lang.reflect.Constructor
import java.sql.ResultSet

object Processor {
    @Suppress("UNCHECKED_CAST")
    fun <T> createEntityList(entityClass: Class<T>, rs: ResultSet): List<T> {
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

                val columnName = if (field.isAnnotationPresent(Column::class.java)) {
                    field.getAnnotation(Column::class.java).name
                } else {
                    field.name
                }

                when (field.type) {
                    Integer::class.java -> field.set(entity, rs.getInt(columnName))
                    String::class.java -> field.set(entity, rs.getString(columnName))
                    Long::class.java -> field.set(entity, rs.getLong(columnName))
                }
            }

            entities.add(entity as T)
        }

        return entities
    }

    private fun chooseConstructor(clazz: Class<*>): Constructor<*> {
        var constructor = clazz.constructors[0]

        for (cons in clazz.constructors) {
            // 优先选择无参构方法
            if (cons.parameterCount == 0) {
                constructor = cons
            }
        }

        return constructor
    }
}