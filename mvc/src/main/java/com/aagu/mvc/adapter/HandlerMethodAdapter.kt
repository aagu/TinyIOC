package com.aagu.mvc.adapter

import com.aagu.mvc.annotation.PathVar
import com.aagu.mvc.annotation.RequestParam
import com.aagu.mvc.mapping.HandlerMethod
import com.aagu.mvc.view.ModelAndView
import com.aagu.mvc.view.ModelMap
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set


class HandlerMethodAdapter : HandlerAdapter {
    override fun handleMV(req: HttpServletRequest, resp: HttpServletResponse, handler: HandlerMethod): ModelAndView? {
        val paramValues = resolveParams(req, resp, handler)

        val result: Any = handler.method.invoke(handler.bean, *paramValues)

        val isModelAndView = handler.method.returnType === ModelAndView::class.java
        return when {
            result is Void -> {
                null
            }
            isModelAndView -> {
                result as ModelAndView
            }
            result is String -> {
                val model = ModelAndView()
                model.model = ModelMap("String", result)
                model
            }
            else -> {
                null
            }
        }
    }

    override fun handleRest(req: HttpServletRequest, resp: HttpServletResponse, handler: HandlerMethod): Any? {
        val paramValues = resolveParams(req, resp, handler)

        val result: Any = handler.method.invoke(handler.bean, *paramValues)

        return if (result is Void) null
            else result
    }

    private fun resolveParams(req: HttpServletRequest, resp: HttpServletResponse, handler: HandlerMethod): Array<Any?> {
        //把方法的形参列表和request的参数列表所在顺序进行对应
        val paramIndexMapping = HashMap<String, Int>()

        val methodParams = handler.method.parameters

        for ((idx, param) in methodParams.withIndex()) {
            for (a in param.annotations) {
                if (a is RequestParam) {
                    var paramName: String = a.name
                    if ("" == paramName.trim { it <= ' ' }) {
                        paramName = param.name
                    }
                    paramIndexMapping[paramName] = idx
                    break
                }
                if (a is PathVar) {
                    var paramName: String = a.name
                    if ("" == paramName.trim { it <= ' ' }) {
                        paramName = param.name
                    }
                    paramIndexMapping[paramName] = idx
                    break
                }
            }
        }

        //提取方法中的request和response参数
        val paramsTypes: Array<Class<*>> = handler.method.parameterTypes
        for (i in paramsTypes.indices) {
            val type = paramsTypes[i]
            if (type == HttpServletRequest::class.java ||
                type == HttpServletResponse::class.java
            ) {
                paramIndexMapping[type.name] = i
            }
        }

        //获得方法的形参列表
        val params: Map<String?, Array<String?>?> = req.parameterMap

        //实参列表
        val paramValues = arrayOfNulls<Any>(paramsTypes.size)

        for ((k, v) in params) {
            val value = Arrays.toString(v).replace("[\\[\\]]".toRegex(), "")
                .replace("\\s".toRegex(), ",")
            if (!paramIndexMapping.containsKey(k)) {
                continue
            }
            val index: Int = paramIndexMapping[k]!!
            paramValues[index] = parseValue(value, paramsTypes[index])
        }

        val pathVarMap = handler.getWildcardValues()
        if (pathVarMap != null) {
            for ((k, v) in pathVarMap) {
                if (!paramIndexMapping.containsKey(k)) {
                    continue
                }
                val index: Int = paramIndexMapping[k]!!
                paramValues[index] = parseValue(v, paramsTypes[index])
            }
        }

        if (paramIndexMapping.containsKey(HttpServletRequest::class.java.name)) {
            val reqIndex = paramIndexMapping[HttpServletRequest::class.java.name]!!
            paramValues[reqIndex] = req
        }

        if (paramIndexMapping.containsKey(HttpServletResponse::class.java.name)) {
            val respIndex = paramIndexMapping[HttpServletResponse::class.java.name]!!
            paramValues[respIndex] = resp
        }

        return paramValues
    }

    private fun parseValue(value: String, paramsType: Class<*>): Any? {
        if (String::class.java == paramsType) {
            return value
        }
        //如果是int
        if (Int::class.java == paramsType) {
            return Integer.valueOf(value)
        } else if (Double::class.java == paramsType) {
            return java.lang.Double.valueOf(value)
        }
        return value
    }
}