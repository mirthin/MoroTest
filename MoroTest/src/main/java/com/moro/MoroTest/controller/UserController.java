package com.moro.MoroTest.controller;

import com.moro.MoroTest.dao.MyUser;
import com.moro.MoroTest.exception.ResourceNotFoundException;
import com.moro.MoroTest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<MyUser> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public MyUser getUser(@PathVariable Long id) {
        MyUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return user;
    }

    @PostMapping
    public MyUser createUser( @RequestBody @Valid MyUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public MyUser updateUser(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable Long id, @RequestBody @Valid MyUser userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        MyUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getUserName().equals(username)) {
            user.setName(userDetails.getName());
            user.setUserName(userDetails.getUserName());

            // Optionally update the password if it is provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
        } else {
            throw new AccessDeniedException("You do not have permission to delete this user");
        }

        return userRepository.save(user);
    }

    @PutMapping("/{id}/password")
    public MyUser updatePassword(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable Long id,  @RequestBody @Valid String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        MyUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getUserName().equals(username)) {
            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            throw new AccessDeniedException("You do not have permission to delete this user");
        }
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the username of the authenticated user

        MyUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getUserName().equals(username)) {
            userRepository.delete(user);
        } else {
            throw new AccessDeniedException("You do not have permission to delete this user");
        }
    }
}
