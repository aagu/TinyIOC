package com.aagu.data.util

import com.aagu.data.sql.SqlBuilder

object SqlUtils {
    fun prepareSql(sql: String, args: Map<String, Any>): String {
        val sqlBuilder = SqlBuilder(sql)
        return sqlBuilder.build(args)
    }
}