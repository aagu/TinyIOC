package com.aagu.data.exception

class NotEntityException(className: String) : RuntimeException("$className is not an Entity")