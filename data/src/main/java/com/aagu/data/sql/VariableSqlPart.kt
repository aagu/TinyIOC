package com.aagu.data.sql

class VariableSqlPart(val placeHolder: String) : SqlPart {
    override fun getString(): String {
        return placeHolder
    }
}