package com.aagu.mapper.exception

class NotEntityException(className: String) : RuntimeException("$className is not an Entity")