package com.aagu.mvc.util

object Serializer {
    fun getSerializedString(target: Any?): String {
        if (target == null) return "null"
        if (target is Int) return "$target"
        if (target is String) return target
        if (target is Boolean) return "$target"
        return serializeObject(target)
    }

    private fun serializeObject(target: Any): String {
        if (target is Map<*,*>) {
            return serializeMap(target)
        }
        val clazz = target::class.java
        val fields = clazz.declaredFields
        val builder = StringBuilder()
        builder.append("{")
        for (field in fields) {
            field.isAccessible = true
            builder.append("\"").append(field.name).append("\":")
            builder.append("\"").append(getSerializedString(field.get(target))).append("\"")
            builder.append(",")
        }
        builder.setCharAt(builder.length - 1, '}')
        return builder.toString()
    }

    private fun serializeMap(target: Map<*, *>): String {
        val builder = StringBuilder()
        builder.append("{")
        for (entry in target.entries) {
            builder.append("\"").append(entry.key).append("\":")
            builder.append("\"").append(getSerializedString(entry.value)).append("\"")
            builder.append(",")
        }
        builder.setCharAt(builder.length - 1, '}')
        return builder.toString()
    }
}