package com.aagu.ioc.util

import org.dom4j.Element
import org.dom4j.io.SAXReader

class XmlScanner {
    private val xmlFiles = ArrayList<String>()
    private lateinit var filter: Filter
    private lateinit var listener: Listener
    private val reader = SAXReader()

    fun setFilter(filter: Filter) {
        this.filter = filter
    }

    fun getFilter(): Filter {
        return this.filter
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun getListener(): Listener {
        return listener
    }

    fun addXmlFile(fileName: String) {
        xmlFiles.add(fileName)
    }

    fun clearXmlFile() {
        xmlFiles.clear()
    }

    fun scan() {
        for (f in xmlFiles) {
            scan(f)
        }
    }

    private fun scan(fileName: String) {
        val document =  reader.read(fileName)
        val nodeList = if (filter.filterByTag().isBlank()) document.rootElement.elements(filter.filterByTag())
            else document.rootElement.elements()
        for (i in 0 until nodeList.size) {
            val node = nodeList[i]
            listener.onScanNode(node)
        }
    }

    companion object{
        interface Filter{
            fun accept(node: Element): Boolean
            fun filterByTag(): String
        }

        interface Listener {
            fun onScanNode(node: Element)
        }
    }
}