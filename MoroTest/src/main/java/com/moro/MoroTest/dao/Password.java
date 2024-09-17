package com.moro.MoroTest.dao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Password {

    @NotNull(message = "Password is mandatory")
    @Size(min = 8, max = 50, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+=-]).{8,}$",
            message = "Password must contain at least one letter, one number, and one special character")
    private String password;

    // Getter and Setter
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
