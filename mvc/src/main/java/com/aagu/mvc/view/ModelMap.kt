package com.aagu.mvc.view


class ModelMap() : LinkedHashMap<String, Any?>() {

    constructor(attributeName: String, attributeValue: Any?) : this() {
        addAttribute(attributeName, attributeValue)
    }

    /**
     * 根据名称添加属性
     * @param attributeName 名称
     * @param attributeValue 属性
     */
    fun addAttribute(attributeName: String, attributeValue: Any?): ModelMap {
        put(attributeName, attributeValue)
        return this
    }

    /**
     * 增加提供的map
     * @see .addAttribute
     */
    fun addAllAttributes(attributes: Map<String, *>): ModelMap {
        putAll(attributes)
        return this
    }

    /**
     * 根据key值合并提供的map
     */
    fun mergeAttributes(attributes: Map<String, *>): ModelMap {
        attributes.forEach { (key: Any?, value: Any?) ->
            if (!containsKey(key)) {
                put(key, value)
            }
        }
        return this
    }

    /**
     * 判断此名称是否存在于map中
     */
    fun containsAttribute(attributeName: String): Boolean {
        return containsKey(attributeName)
    }
}