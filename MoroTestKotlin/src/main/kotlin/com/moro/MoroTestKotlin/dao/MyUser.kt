package com.moro.MoroTestKotlin.dao


import com.moro.MoroTestKotlin.model.Role
import jakarta.persistence.*
import jakarta.validation.constraints.*
import lombok.Data
import java.util.Objects

/**
 * Entity class representing a user in the system.
 */
@Data
@Entity
@Table(name = "user_table")
data class MyUser(

    /**
     * The unique identifier for the user.
     */
    @field:Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    /**
     * The name of the user.
     * It must be between 2 and 50 characters.
     */
    @field:NotNull(message = "Name is required")
    @field:NotEmpty(message = "Name is required")
    @field:Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(name = "name")
    var name: String,

    /**
     * The username of the user, which must be unique.
     * It must be between 2 and 50 characters.
     */
    @field:NotNull(message = "Username is required")
    @field:NotEmpty(message = "Username is required")
    @field:Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @Column(name = "username", unique = true)
    var username: String,

    /**
     * The password of the user.
     * It must be at least 8 characters long and contain at least one letter, one number, and one special character.
     */
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+=-]).{8,}$",
        message = "Password must contain at least one letter, one number, and one special character"
    )
    @Column(name = "password")
    var password: String?,

    /**
     * The role of the user, which defines their permissions.
     */
    @Enumerated(EnumType.STRING)
    var role: Role = Role.ROLE_USER
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as MyUser

        return id == other.id &&
                name == other.name &&
                username == other.username &&
                password == other.password
    }

    override fun hashCode(): Int {
        return Objects.hash(id, name, username, password)
    }

    override fun toString(): String {
        return "MyUser(id=$id, name='$name', username='$username', password='$password', role=$role)"
    }
}