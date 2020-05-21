package com.aagu.data.sql

class VariableSqlPart(val placeHolder: String, val index: Int) : SqlPart {
    override fun getString(): String {
        return placeHolder
    }

    fun getIndexedArgName(): String {
        return "arg$index"
    }
}