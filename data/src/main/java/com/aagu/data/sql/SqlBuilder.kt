package com.aagu.data.sql

import java.util.*

class SqlBuilder(private val sql: String) {
    private val sqlParts = LinkedList<SqlPart>()
    private var isParsed = false

    fun build(args: Map<String, Any>): String {
        if (!isParsed) {
            parse()
            isParsed = true
        }
        val builder = StringBuilder()

        var varIdx = 0
        for (part in sqlParts) {
            if (part is StaticSqlPart) builder.append(part.value)
            else if (part is VariableSqlPart){
                val realValue = args[part.placeHolder]
                varIdx++
                builder.append(realValue.toString())
            }
        }
        return builder.toString()
    }

    private fun parse() {
        val chars = sql.toCharArray()

        // 普通字符 -> 0
        // '$' -> 1
        var state = 0

        val builder = StringBuilder()

        for (i in chars.indices) {
            when {
                chars[i] == '$' -> {
                    if (state == 0) state = 1
                    builder.append('$')
                }
                chars[i].isLetterOrDigit() -> {
                    if (state == 1) {
                        // static end with '$'
                        builder.setLength(builder.length - 1) // delete '$'
                        sqlParts.add(StaticSqlPart(builder.toString()))
                        builder.setLength(0) // clear it
                        state = 2
                    }
                    builder.append(chars[i])
                }
                else -> {
                    if (state == 2) {
                        // variable end
                        sqlParts.add(VariableSqlPart(builder.toString()))
                        state = 0
                        builder.setLength(0) // clear it
                    }
                    builder.append(chars[i])
                }
            }
        }

        if (builder.isNotEmpty()) {
            if (state == 2) {
                // variable end
                sqlParts.add(VariableSqlPart(builder.toString()))
                builder.setLength(0) // clear it
            } else if (state == 0) {
                sqlParts.add(StaticSqlPart(builder.toString()))
                builder.setLength(0) // clear it
            }
        }
    }

    /**
     * 获取原始sql
     * @return sql
     */
    fun getOriginalSql(): String {
        return sql
    }
}