package com.aagu.data.proxy

data class Command(val type:String, val sql:String) {
    var entityClass: Class<*>? = null

    companion object {
        const val DELETE = "delete"
        const val INSERT = "insert"
        const val SELECT = "select"
        const val UPDATE = "update"
    }
}