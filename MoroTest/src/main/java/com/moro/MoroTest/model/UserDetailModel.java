package com.moro.MoroTest.model;

import com.moro.MoroTest.dao.MyUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class UserDetailModel implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserDetailModel(MyUser user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();

        this.authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return this.authorities; }

}