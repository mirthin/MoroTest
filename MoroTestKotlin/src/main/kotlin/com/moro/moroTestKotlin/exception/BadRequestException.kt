package com.moro.moroTestKotlin.exception

class BadRequestException(field: String) : RuntimeException("$field is required or invalid")