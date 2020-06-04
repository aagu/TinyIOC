package com.aagu.mvc.mapping

import com.aagu.ioc.context.ApplicationContext
import com.aagu.mvc.annotation.Controller
import com.aagu.mvc.annotation.RequestMapping
import com.aagu.mvc.annotation.ResponseBody
import com.aagu.mvc.annotation.RestController
import com.aagu.mvc.util.Trie
import java.lang.reflect.Method
import javax.servlet.http.HttpServletRequest

class HandlerMethodMapping(context: ApplicationContext) : HandlerMapping {
    private val mappingRegistry = MappingRegistry()

    init {
        initHandleMethod(context)
    }

    override fun getHandler(req: HttpServletRequest): HandlerMethod? {
        var url:String = req.requestURI
        val contextPath = req.contextPath

        url = url.replace(contextPath, "").replace("+/", "/")

        val handler = mappingRegistry.getHandlerMethod(url)

        if (handler != null) {
            if (handler.checkHandleType(req.method)) {
                return handler
            } else {
                println("${req.requestURI} does not support request type ${req.method}")
            }
        }
        return null
    }

    private fun initHandleMethod(context: ApplicationContext) {
        val beanNames = context.getRegisteredBeanNames()

        try {
            for (beanName in beanNames) {
                val bean: Any = context.getBean(beanName)
                val clazz: Class<*> = bean.javaClass
                //只对加了@Controller注解的类进行初始化
                if (!(clazz.isAnnotationPresent(Controller::class.java) ||
                    clazz.isAnnotationPresent(RestController::class.java))) {
                    continue
                }
                var baseUrl = ""
                var isBody = clazz.isAnnotationPresent(RestController::class.java)
                //获取Controller的url配置
                if (clazz.isAnnotationPresent(RequestMapping::class.java)) {
                    val requestMapping: RequestMapping = clazz.getAnnotation(RequestMapping::class.java)
                    baseUrl = requestMapping.value
                }
                //获取Method的url配置
                val methods = clazz.declaredMethods
                for (method in methods) { //没有加RequestMapping注解的直接忽略
                    if (!method.isAnnotationPresent(RequestMapping::class.java)) {
                        continue
                    }
                    //映射URL
                    val requestMapping: RequestMapping = method.getAnnotation(RequestMapping::class.java)
                    var pattern =
                        "/$baseUrl/${requestMapping.value}".replace(
                            "/+".toRegex(),
                            "/"
                        )
                    pattern = pattern.removePrefix("/")
                    pattern = pattern.removeSuffix("/")
                    isBody = isBody or method.isAnnotationPresent(ResponseBody::class.java)
                    mappingRegistry.register(pattern, bean, method, isBody)
                    println("Mapped $pattern, ${method.declaringClass.canonicalName}.${method.name}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class MappingRegistry {
        private val registry: HashMap<String, HandlerMethod> = HashMap()
        private val mappingLookup: Trie = Trie()

        fun register(mapping: String, handler: Any, method: Method, responseBody: Boolean) {
            val handlerMethod = createHandlerMethod(handler, method, responseBody)

            registry[mapping] = handlerMethod
            mappingLookup.insert(mapping)
        }

        fun getHandlerMethod(mapping: String): HandlerMethod? {
            var pattern = mapping.removePrefix("/")
            pattern = pattern.removeSuffix("/")
            val node = mappingLookup.search(pattern)
            if (node != null) {
                val handler = registry[node.pattern]
                if (handler != null) {
                    val arguments = node.pattern.split("/")
                    val values = pattern.split("/")
                    for ((idx, value) in arguments.withIndex()) {
                        if (value.startsWith(":") || value.startsWith("*")) {
                            handler.addWildcardValue(value, values[idx])
                        }
                    }
                }
                return handler
            }
            return null
        }

        private fun createHandlerMethod(
            handler: Any,
            method: Method,
            responseBody: Boolean
        ): HandlerMethod {
            return HandlerMethod(handler, method, responseBody)
        }
    }
}