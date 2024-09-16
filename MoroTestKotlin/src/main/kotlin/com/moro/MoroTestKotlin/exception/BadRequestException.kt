package com.moro.MoroTestKotlin.exception

class BadRequestException(field: String) : RuntimeException("$field is required or invalid")