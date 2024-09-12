package com.moro.MoroTest.controller;

import com.moro.MoroTest.dao.MyUser;
import com.moro.MoroTest.dao.Password;
import com.moro.MoroTest.service.MyUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for managing users.
 */
@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MyUserService myUserService;

    @GetMapping()
    public ResponseEntity<?> getUsers() {
        List<MyUser> users = myUserService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return myUserService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid MyUser user) {
        myUserService.addUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody @Valid MyUser userDetails) {
        MyUser user = myUserService.validateAndRetrieveUser(id);
        myUserService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body("User updated: " + user);
    }

    @PutMapping("/password/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @Valid @RequestBody Password newPassword) {
        MyUser user = myUserService.validateAndRetrieveUser(id);
        user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
        myUserService.updateUser(user);
        return new ResponseEntity<>("Password updated for user: " + user, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        MyUser user = myUserService.validateAndRetrieveUser(id);
        myUserService.deleteUser(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body("User successfully deleted");
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.status(HttpStatus.OK).body("Test success");
    }

    @DeleteMapping("/deleteall")
    public void deleteAllUsers() {
        myUserService.deleteAllUsers();
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminAccess() {
        return "Access granted to admin!";
    }
}