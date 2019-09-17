package com.aagu.ioc.util

import java.util.regex.Pattern

class StringUtils {
    companion object {
        fun isEmpty(string: String?): Boolean {
            return string == null || string == ""
        }

        fun isNotEmpty(string: String?): Boolean {
            return string != null && string != ""
        }

        fun lowerCaseFirstChar(string: String):String {
            val firstChar = string.toCharArray()[0]
            return if (firstChar.isLowerCase()) string
            else firstChar.toLowerCase() + string.substring(1, string.length)
        }

        fun getValueFromRegex(string: String, regex: String): String? {
            val matcher = Pattern.compile(regex).matcher(string)
            if (matcher.find()) {
                return matcher.group(1)
            }
            return null
        }

        fun getGroupsFromRegex(string: String, regex: String): Array<String?> {
            val matcher = Pattern.compile(regex).matcher(string)
            if (matcher.find()) {
                val groups = arrayOfNulls<String>(matcher.groupCount())
                for (i in 1..matcher.groupCount()) {
                    groups[i-1] = matcher.group(i)
                }
                return groups
            }
            return emptyArray()
        }
    }
}