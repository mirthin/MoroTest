package com.moro.moroTestKotlin.exception

class UserAlreadyExistsException(username : String) : RuntimeException("User with username $username already exists.")