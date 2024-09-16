package com.moro.MoroTestKotlin.model

import com.moro.MoroTestKotlin.dao.MyUser
import lombok.Data
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Data
class UserDetailModel(user: MyUser) : UserDetails {
    private val id: Long = user.id
    private val username: String = user.username
    private val password: String? = user.password

    private val authorities: MutableList<GrantedAuthority> = ArrayList()

    init {
        authorities.add(SimpleGrantedAuthority(user.role.name))
    }

    override fun getAuthorities(): kotlin.collections.Collection<GrantedAuthority> {
        return this.authorities
    }

    override fun getPassword(): String? {
        return this.password
    }

    override fun getUsername(): String {
        return this.username
    }
}