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

/**
 * Entity class representing a user in the system.
 */
@Data
@ToString
@Table(name = "user_table")
@Entity
public class MyUser {

    /**
     * The unique identifier for the user.
     */
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the user.
     * It must be between 2 and 50 characters.
     */
    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(name = "name")
    private String name;

    /**
     * The username of the user, which must be unique.
     * It must be between 2 and 50 characters.
     */
    @NotNull(message = "Username is required")
    @NotEmpty(message = "Username is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(name = "username", unique = true)
    private String username;

    /**
     * The password of the user.
     * It must be at least 8 characters long and contain at least one letter, one number, and one special character.
     */
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+=-]).{8,}$",
            message = "Password must contain at least one letter, one number, and one special character")
    @Column(name = "password")
    private String password;

    /**
     * The role of the user, which defines their permissions.
     */
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