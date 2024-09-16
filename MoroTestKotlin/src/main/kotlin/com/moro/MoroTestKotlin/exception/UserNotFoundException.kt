package com.moro.MoroTestKotlin.exception

class UserNotFoundException(id: Long) : RuntimeException("User not found with id: $id")