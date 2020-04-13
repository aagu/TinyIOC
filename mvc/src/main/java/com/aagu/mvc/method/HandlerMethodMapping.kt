package com.aagu.mvc.method

import com.aagu.ioc.context.ApplicationContext
import com.aagu.mvc.HandlerMapping
import com.aagu.mvc.annotation.Controller
import com.aagu.mvc.annotation.RequestMapping
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

        return mappingRegistry.getHandlerMethod(url)
    }

    private fun initHandleMethod(context: ApplicationContext) {
        val beanNames = context.getRegisteredBeanNames()

        try {
            for (beanName in beanNames) {
                val bean: Any = context.getBean(beanName)
                val clazz: Class<*> = bean.javaClass
                //只对加了@Controller注解的类进行初始化
                if (!clazz.isAnnotationPresent(Controller::class.java)) {
                    continue
                }
                var baseUrl = ""
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
                    //  /demo/query 字符串处理
//  (//demo//query)
                    val regex =
                        ("/" + baseUrl + "/" + requestMapping.value.replace("\\*", ".*")).replace(
                            "/+".toRegex(),
                            "/"
                        )
                    mappingRegistry.register(regex, bean, method)
                    println("Mapped $regex, ${method.returnType} ${method.name}")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class MappingRegistry {
        private val registry: HashMap<String, MappingRegistration<String>> = HashMap()
        private val mappingLookup: HashMap<String, HandlerMethod> = HashMap()

        fun register(mapping: String, handler: Any, method: Method) {
            val handlerMethod = createHandlerMethod(handler, method)

            mappingLookup[mapping] = handlerMethod
        }

        fun getHandlerMethod(mappings: String): HandlerMethod? {
            return mappingLookup[mappings]
        }

        private fun createHandlerMethod(handler: Any, method: Method): HandlerMethod {
            return HandlerMethod(handler, method)
        }
    }

    companion object {
        private data class MappingRegistration<T>(val mapping: T, val handlerMethod: HandlerMethod)
    }
}