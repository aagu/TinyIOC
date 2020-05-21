package com.aagu.http

import com.aagu.mvc.annotation.Controller
import com.aagu.mvc.annotation.RequestMapping

@Controller
class SimpleController {

    @RequestMapping("/")
    fun index(): String {
        return "Hello World"
    }
}