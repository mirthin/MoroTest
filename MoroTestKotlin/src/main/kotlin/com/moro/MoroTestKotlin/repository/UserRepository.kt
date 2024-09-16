package com.moro.MoroTestKotlin.repository

import com.moro.MoroTestKotlin.dao.MyUser
import com.moro.MoroTestKotlin.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<MyUser, Long> {
    fun findByUsername(username: String): Optional<MyUser>

}