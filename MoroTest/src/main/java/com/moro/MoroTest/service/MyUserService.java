package com.moro.MoroTest.service;


import com.moro.MoroTest.dao.Password;
import com.moro.MoroTest.exception.BadRequestException;
import com.moro.MoroTest.exception.UserAlreadyExistException;
import com.moro.MoroTest.exception.UserNotFoundException;
import com.moro.MoroTest.model.Role;
import com.moro.MoroTest.model.UserDetailModel;
import com.moro.MoroTest.repository.UserRepository;
import com.moro.MoroTest.dao.MyUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

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

    public void addUser(MyUser user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistException(user.getUsername());
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BadRequestException("Password");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign role to first user as ADMIN, others as USER
        assignRoleToUser(user);
        userRepository.save(user);
    }

    public void updateUser(MyUser oldUserDetails, MyUser newUserDetails) {
        oldUserDetails.setName(newUserDetails.getName());
        oldUserDetails.setUsername(newUserDetails.getUsername());

        // Optionally update the password if provided
        if (newUserDetails.getPassword() != null && !newUserDetails.getPassword().isEmpty()) {
            oldUserDetails.setPassword(passwordEncoder.encode(newUserDetails.getPassword()));
        }

        // Only admin can change roles
        if (isUserAdmin(SecurityContextHolder.getContext().getAuthentication().getName())) {
            if(newUserDetails.getRole() != null) {
                oldUserDetails.setRole(newUserDetails.getRole());
            }
        }

        userRepository.save(oldUserDetails);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public boolean isUserAdmin(String username) {
        Optional<MyUser> userOpt = userRepository.findByUsername(username);
        return userOpt.map(user -> user.getRole() == Role.ROLE_ADMIN).orElse(false);
    }

    public MyUser validateAndRetrieveUser(Long id) {
        return getUserById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void assignRoleToUser(MyUser user) {
        if (getAllUsers().isEmpty()) {
            user.setRole(Role.ROLE_ADMIN);
        } else {
            user.setRole(Role.ROLE_USER);
        }
    }

    public void updateUserPassword(MyUser user, Password newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
        userRepository.save(user);
    }
}