package com.aagu.ioc.util

import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URL
import java.net.URLDecoder
import java.util.*
import java.util.jar.JarFile
import kotlin.collections.ArrayList

class PackageScanner {
    var recursive = true
    private val packageNames = ArrayList<String>()
    private val filters =  ArrayList<Filter>()

    fun addFilter(filter: Filter) {
        filters.add(filter)
    }

    fun addFilters(filtersToAdd: Collection<Filter>) {
        filters.addAll(filtersToAdd)
    }

    fun getFilters(): List<Filter> {
        return filters
    }

    fun clearFilters() {
        filters.clear()
    }

    fun addPackage(packageName: String) {
        require(packageName.matches(Regex("[\\w]+(\\.[\\w]+)*"))) { "非法包名" }
        this.packageNames.add(packageName)
    }

    fun clearPackage() {
        this.packageNames.clear()
    }

    fun scan() {
        for (name in packageNames) {
            scan(name)
        }
    }

    private fun onScanClass(clazz: Class<*>) {
        for (filter in filters) {
            filter.onFilter(clazz)
        }
    }

    private fun scan(name: String) {
        val dirName = name.replace('.', '/')
        var dirs: Enumeration<URL>? = null
        try {
            dirs = Thread.currentThread().contextClassLoader.getResources(dirName)
            while (dirs.hasMoreElements()) {
                val url = dirs.nextElement()
                val protocol = url.protocol
                if ("file" == protocol) {
                    val filePath = URLDecoder.decode(url.file, "UTF-8")
                    findAndAddClassesInPackageByFile(name, filePath)
                } else if ("jar" == protocol) {
                    var jar: JarFile
                    try {
                        jar = (url.openConnection() as JarURLConnection).jarFile
                        val entries = jar.entries()
                        while (entries.hasMoreElements()) {
                            val entry = entries.nextElement()
                            var entryName = entry.name
                            if (name.regionMatches(0, "/", 0, 1)) {
                                entryName = entryName.substring(1)
                            }
                            if (entryName.startsWith(dirName)) {
                                val idx = name.lastIndexOf("/")
                                var pkgName = name
                                if (idx != -1) {
                                    pkgName = name.substring(0, idx).replace('/','.')
                                }
                                if (idx != -1 || recursive) {
                                    if (entryName.endsWith(".class") && entry.isDirectory) {
                                        val className = entryName.substring(
                                                pkgName.length + 1,
                                                entryName.length - 6)
                                        try {
                                            val clazz = Thread.currentThread().contextClassLoader
                                                    .loadClass("$pkgName.$className")
                                            onScanClass(clazz)
                                        } catch (e: ClassNotFoundException) {
                                            System.err.println("类加载出错")
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: IOException) {
                        System.err.println("在扫描用户定义视图时从jar包获取文件出错")
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            System.err.println("扫描出错")
            e.printStackTrace()
        }
    }

    private fun findAndAddClassesInPackageByFile(packageName: String, filePath: String?) {
        val dir:File = File(filePath!!)
        if (!dir.exists() || !dir.isDirectory) {
            System.err.println("用户定义包名 $packageName 下没有任何文件")
            return
        }
        val dirFiles = dir.listFiles { file -> ((recursive && file.isDirectory) || (file.name.endsWith(".class"))) }
        for (file in dirFiles!!) {
            if (file.isDirectory) {
                findAndAddClassesInPackageByFile("$packageName.${file.name}", file.absolutePath)
            } else {
                val className = file.name.substring(0, file.name.length - 6)
                try {
                    val clazz = Thread.currentThread().contextClassLoader
                            .loadClass("$packageName.$className")
                    onScanClass(clazz)
                } catch (e: ClassNotFoundException) {
                    System.err.println("添加用户自定义视图类错误 找不到此类的.class文件")
                    e.printStackTrace()
                }
            }
        }
    }

    interface Filter {
        fun onFilter(clazz: Class<*>)
    }
}