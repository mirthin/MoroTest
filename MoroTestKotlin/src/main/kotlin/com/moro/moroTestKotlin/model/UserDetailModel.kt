package com.moro.moroTestKotlin.model

import com.moro.moroTestKotlin.dao.MyUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


data class UserDetailModel(val user: MyUser) : UserDetails {
    val id: Long = user.id
    private val username: String = user.username
    private val password: String? = user.password

    private val authorities: MutableList<GrantedAuthority> = ArrayList()

    init {
        authorities.add(SimpleGrantedAuthority(user.role.name))
    }

    override fun getAuthorities(): kotlin.collections.Collection<GrantedAuthority> = this.authorities
    override fun getPassword(): String? = this.password
    override fun getUsername(): String = this.username
}