package com.moro.MoroTest.service;


import com.moro.MoroTest.model.Role;
import com.moro.MoroTest.repository.UserRepository;
import com.moro.MoroTest.dao.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.core.userdetails.User.withUsername;

@Service
public class MyUserService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        UserBuilder builder = withUsername(user.getUserName());
        builder.password(user.getPassword());
        builder.roles("USER"); // For simplicity, assigning all users the "USER" role
        return builder.build();
    }

    public MyUser loadUserByUsername2(String userName) throws UsernameNotFoundException {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
    }

    public List<MyUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<MyUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public MyUser addUser(MyUser user) {
        return userRepository.save(user);
    }

    public MyUser updateUser(MyUser user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean isUserAdmin(String userName) {
        Optional<MyUser> userOpt = userRepository.findByUserName(userName);
        return userOpt.map(user -> user.getRole() == Role.ADMIN).orElse(false);
    }

}