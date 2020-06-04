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
        val parts = pattern.split("/")
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
            child.isWildcard = tmp[0] == ':' || tmp[0] == '*'
            currNode.children.add(child)
        }
        doInsert(pattern, parts, height+1, child)
    }

    fun search(pattern: String): TrieNode? {
        val parts = pattern.split("/")
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

    private fun removeSlash(string: String): String {
        var str = string
        if (str.startsWith("/")) {
            str = str.substring(1)
        }
        if (str.endsWith("/")) {
            str = str.substring(0, str.length - 1)
        }
        return str
    }
}