package com.moro.MoroTest.controller;

import com.moro.MoroTest.dao.MyUser;
import com.moro.MoroTest.exception.ResourceNotFoundException;
import com.moro.MoroTest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
        MyUser newUser = user;
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(newUser);
    }

    @PutMapping("/{id}")
    public MyUser updateUser(@PathVariable Long id, @RequestBody @Valid MyUser userDetails) {
        MyUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setName(userDetails.getName());
/*
        // Optionally update the password if it is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
*/
        user.setPassword(userDetails.getPassword());

        return userRepository.save(user);
    }

    @PatchMapping("/{id}/password")
    public MyUser updatePassword(@PathVariable Long id,  @RequestBody @Valid String newPassword) {
        MyUser user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        /*
        // Optionally update the password if it is provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        */
        user.setPassword(passwordEncoder.encode(newPassword));

        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable Long id) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();

            try {
                // Decode Base64
                String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
                // Split into username and password
                String[] values = credentials.split(":", 2);
                if (values.length == 2) {
                    String username = values[0];
                    String password = values[1];
                    MyUser user = userRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
                    if(user.getUserName().equals(username) && passwordEncoder.matches(password, user.getPassword())) {
                        userRepository.delete(user);
                        return;
                    }
                }
                throw new BadCredentialsException("Bad credentials");
            } catch (IllegalArgumentException e) {
                throw new BadCredentialsException("Bad credentials");
            }
        }
    }
}
