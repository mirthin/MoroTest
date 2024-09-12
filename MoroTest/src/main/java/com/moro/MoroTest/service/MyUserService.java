package com.moro.MoroTest.service;


import com.moro.MoroTest.exception.BadRequestException;
import com.moro.MoroTest.exception.UnauthorizedAccessException;
import com.moro.MoroTest.exception.UserNotFoundException;
import com.moro.MoroTest.model.Role;
import com.moro.MoroTest.model.UserDetailModel;
import com.moro.MoroTest.repository.UserRepository;
import com.moro.MoroTest.dao.MyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.core.userdetails.User.withUsername;

@Service
public class MyUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public MyUserService( PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> user = userRepository.findByUsername(username);
        return user.map(UserDetailModel::new).orElseThrow(()->new UsernameNotFoundException("Invalid Username"));
    }

    public Optional<MyUser> getUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public List<MyUser> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<MyUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public MyUser addUser(MyUser user) {
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign role to first user as ADMIN, others as USER
        assignRoleToUser(user);
        return userRepository.save(user);
    }

    public MyUser updateUser(MyUser user) {
        // Optionally update the password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Only admin can change roles
        if (isUserAdmin(SecurityContextHolder.getContext().getAuthentication().getName())) {
            user.setRole(user.getRole());
        }


        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public boolean isUserAdmin(String username) {
        Optional<MyUser> userOpt = userRepository.findByUsername(username);
        return userOpt.map(user -> user.getRole() == Role.ADMIN).orElse(false);
    }

    public MyUser validateAndRetrieveUser(Long id) {
        return getUserById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new BadRequestException("Password");
        }
    }

    public void assignRoleToUser(MyUser user) {
        if (getAllUsers().isEmpty()) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
    }
}