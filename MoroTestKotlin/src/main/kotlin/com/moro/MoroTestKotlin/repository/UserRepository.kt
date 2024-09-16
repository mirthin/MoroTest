package com.moro.MoroTestKotlin.repository

import com.moro.MoroTestKotlin.dao.MyUser
import com.moro.MoroTestKotlin.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<MyUser, Long> {
    fun findByUsername(username: String): Optional<MyUser>

    // Custom query method to find users by role
    fun findByRole(role: Role?): List<MyUser>

    // Method to find users with ADMIN role
    fun findAdmins(): List<MyUser> {
        return findByRole(Role.ROLE_ADMIN)
    }
}