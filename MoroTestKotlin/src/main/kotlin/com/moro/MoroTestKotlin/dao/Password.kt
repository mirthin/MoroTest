package com.moro.MoroTestKotlin.dao

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class Password(

    @field:NotNull(message = "Password is mandatory")
    @field:Size(min = 8, max = 50, message = "Password must be at least 8 characters long")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+=-]).{8,}$",
        message = "Password must contain at least one letter, one number, and one special character"
    )
    var password: String
)