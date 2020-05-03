package com.aagu.mvc.annotation

import com.aagu.mvc.util.RequestMethod

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequestMapping(val value: String = "",
                                val type: Array<RequestMethod> = [RequestMethod.GET, RequestMethod.POST,
                                    RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH])