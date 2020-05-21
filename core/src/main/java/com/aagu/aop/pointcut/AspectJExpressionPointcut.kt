package com.aagu.aop.pointcut

import com.aagu.aop.util.AopProxyUtils
import org.aspectj.weaver.tools.PointcutExpression
import org.aspectj.weaver.tools.PointcutParser
import java.lang.reflect.Method

class AspectJExpressionPointcut(private val expression: String): Pointcut {
    private val pe: PointcutExpression
    var isAnnotationPointcut: Boolean = false
        private set
    var annotation: Class<out Annotation>? = null

    init {
        pe = pp.parsePointcutExpression(expression)
        isAnnotationPointcut = AopProxyUtils.isAnnotationExpression(expression)
        if (isAnnotationPointcut) {
            annotation = Class.forName(expression.substring(12, expression.length - 1)) as Class<out Annotation>?
        }
    }

    override fun matchMethod(method: Method, targetClass: Class<*>): Boolean {
        if (isAnnotationPointcut) {
            return method.declaringClass == targetClass
        }
        val shadowMatch = pe.matchesMethodExecution(method)
        return shadowMatch.alwaysMatches()
    }

    override fun matchClass(targetClass: Class<*>): Boolean {
        if (isAnnotationPointcut) {
            return targetClass.isAnnotationPresent(annotation)
        }
        return pe.couldMatchJoinPointsInType(targetClass)
    }

    companion object {
        private val pp = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution()
    }
}