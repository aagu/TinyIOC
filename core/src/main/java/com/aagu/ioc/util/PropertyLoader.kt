package com.aagu.ioc.util

import com.aagu.ioc.exception.PropertyNotFoundException
import java.io.BufferedReader
import java.io.FileReader
import java.util.*

object PropertyLoader {
    private val properties: Properties = Properties()
    private var isLoaded = false

    fun load(file: String){
        val bufferedReader = BufferedReader(FileReader(file))
        properties.load(bufferedReader)
        isLoaded = true
    }

    fun unload() {
        isLoaded = false
    }

    fun getConfigs(): Properties {
        return if (isLoaded) properties else Properties()
    }

    fun getProperty(key: String): String? {
        var value: String? = null
        if (isLoaded) {
            try {
                value = properties.getProperty(key)
            } catch (e: IllegalStateException) {

            }
        }
        return value
    }

    @Throws(PropertyNotFoundException::class)
    fun getBooleanProperty(key: String): Boolean {
        val value: String = getProperty(key) ?: throw PropertyNotFoundException(key)

        return value == "true"
    }

    fun getBooleanProperty(key: String, defValue: Boolean): Boolean {
        return try {
            getBooleanProperty(key)
        } catch (ignore: PropertyNotFoundException) {
            defValue
        }
    }
}