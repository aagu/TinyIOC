package com.aagu.data.sql

class StaticSqlPart(val value: String): SqlPart {
    override fun getString(): String {
        return value
    }
}