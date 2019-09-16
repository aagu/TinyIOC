package com.aagu.ioc.util

import java.io.BufferedReader
import java.io.FileReader
import java.util.*

object PropertyLoader {
    private val properties: Properties = Properties()

    init {
        val bufferedReader = BufferedReader(FileReader("app.properties"))
        properties.load(bufferedReader)
    }

    fun getProperty(key: String): String? {
        var value: String? = null
        try {
            value = properties.getProperty(key)
        } catch (e: IllegalStateException) {

        }
        return value
    }
}