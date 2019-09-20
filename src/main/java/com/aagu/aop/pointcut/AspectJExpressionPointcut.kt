package com.aagu.aop.pointcut

import org.aspectj.weaver.tools.PointcutExpression
import org.aspectj.weaver.tools.PointcutParser
import java.lang.reflect.Method

class AspectJExpressionPointcut(private val expression: String): Pointcut {
    private val pe: PointcutExpression

    init {
        pe = pp.parsePointcutExpression(expression)
    }
    override fun matchMethod(method: Method, targetClass: Class<*>): Boolean {
        val shadowMatch = pe.matchesMethodExecution(method)
        return shadowMatch.alwaysMatches()
    }

    override fun matchClass(targetClass: Class<*>): Boolean {
        return pe.couldMatchJoinPointsInType(targetClass)
    }

    companion object {
        private val pp = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution()
    }
}