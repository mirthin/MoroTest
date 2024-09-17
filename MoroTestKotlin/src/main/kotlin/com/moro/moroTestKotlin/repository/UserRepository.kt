package com.moro.moroTestKotlin.repository

import com.moro.moroTestKotlin.dao.MyUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<MyUser, Long> {
    fun findByUsername(username: String): Optional<MyUser>

}