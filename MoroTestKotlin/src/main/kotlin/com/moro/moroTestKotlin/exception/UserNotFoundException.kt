package com.moro.moroTestKotlin.exception

class UserNotFoundException(id: Long) : RuntimeException("User not found with id: $id")