package com.moro.MoroTest.dao;

import com.moro.MoroTest.model.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.ToString;

import java.util.Objects;

@Data
@ToString
@Table(name = "user_table")
@Entity
public class MyUser {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Username is required")
    @NotEmpty(message = "Username is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(name = "username", unique = true)
    private String username;


    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+=-]).{8,}$",
            message = "Password must contain at least one letter, one number, and one special character")
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyUser myUser = (MyUser) o;
        return Objects.equals(id, myUser.id) &&
                Objects.equals(name, myUser.name) &&
                Objects.equals(username, myUser.username) &&
                Objects.equals(password, myUser.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, username, password);
    }

}