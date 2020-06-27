package com.aagu.mvc.util

class Trie {
    inner class TrieNode {
        var pattern: String = ""
        var part: String = ""
        val children: ArrayList<TrieNode> = ArrayList()
        var isWildcard = false
    }

    private val root = TrieNode()

    private fun matchChild(parent: TrieNode, part: String): TrieNode? {
        for (child in parent.children) {
            if (child.part == part || child.isWildcard) {
                return child
            }
        }
        return null
    }

    private fun matchChildren(parent: TrieNode, part: String): ArrayList<TrieNode> {
        val nodes = ArrayList<TrieNode>()
        for (child in parent.children) {
            if (child.part == part || child.isWildcard) {
                nodes.add(child)
            }
        }
        return nodes
    }

    fun insert(pattern: String) {
        val parts = cut(pattern)
        doInsert(pattern, parts, 0, root)
    }

    private fun doInsert(pattern: String, parts: List<String>, height: Int, currNode: TrieNode) {
        if (parts.size == height) {
            currNode.pattern = pattern
            return
        }

        val tmp = parts[height]
        var child = this.matchChild(currNode, tmp)
        if (child == null) {
            child = TrieNode()
            child.part = tmp
            child.isWildcard = tmp.startsWith("/:") || tmp.startsWith("/*")
            currNode.children.add(child)
        }
        doInsert(pattern, parts, height+1, child)
    }

    fun search(pattern: String): TrieNode? {
        val parts = cut(pattern)
        return doSearch(parts, 0, root)
    }

    private fun doSearch(parts: List<String>, height: Int, currNode: TrieNode): TrieNode? {
        if (parts.size == height || currNode.part.startsWith("*")) {
            if (currNode.pattern == "") {
                return null
            }
            return currNode
        }

        val tmp = parts[height]
        val tmpChildren = matchChildren(currNode, tmp)
        for (child in tmpChildren) {
            val result = doSearch(parts, height+1, child)
            if (result != null) {
                return result
            }
        }

        return null
    }

    companion object {
        fun cut(pattern: String): List<String> {
            val parts = ArrayList<String>()
            val builder = StringBuilder()
            var idx = 0
            val chars = pattern.toCharArray()
            while (idx < chars.size ) {
                if (chars[idx] == '/') {
                    builder.append('/')
                    idx++
                    while (idx < chars.size && chars[idx] != '/') {
                        builder.append(chars[idx++])
                    }
                    parts.add(builder.toString())
                    builder.setLength(0)
                }
            }
            return parts
        }
    }
}