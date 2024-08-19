package com.moro.MoroTest.controller;

import com.moro.MoroTest.dao.MyUser;
import com.moro.MoroTest.model.Role;
import com.moro.MoroTest.service.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.*;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MyUserService myUserService;

    @GetMapping()
    public ResponseEntity<?> getUsers(@RequestParam Optional<Long> id) {
        if (id.isPresent()) {
            // Handle single user request
            Long userId = id.get();
            return myUserService.getUserById(userId)
                    .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            // Handle all users request
            List<MyUser> users = myUserService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    //Get single user if there is path variable with ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return myUserService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createUser( @RequestBody @Valid MyUser user) {
        //password cannot be empty
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            //Make sure, that first user is automatically ADMIN
            if(myUserService.getAllUsers().isEmpty()) {
                user.setRole(Role.ADMIN);
            } else {
                user.setRole(Role.USER);
            }

            myUserService.addUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password has to be entered");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?>  updateUser(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable Long id, @RequestBody @Valid MyUser userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserName = authentication.getName();

        MyUser user = myUserService.getUserById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);
        }

        //USER can only update his data, ADMIN can change data for all users
        if (user.getUserName().equals(authenticatedUserName) || myUserService.isUserAdmin(authenticatedUserName)) {
            user.setName(userDetails.getName());
            user.setUserName(userDetails.getUserName());

            // Optionally update the password if it is provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            //only admin can change roles
            if(myUserService.isUserAdmin(user.getUserName())) {
                user.setRole(userDetails.getRole());
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to delete this user");
        }
        myUserService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable Long id, @Valid  @RequestBody String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserName = authentication.getName();

        MyUser user = myUserService.getUserById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);
        }

        //USER can only change his password, ADMIN can change passwords for all users
        if (user.getUserName().equals(authenticatedUserName) || myUserService.isUserAdmin(authenticatedUserName)) {
            user.setPassword(passwordEncoder.encode(newPassword));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to delete this user");
        }
        myUserService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserName = authentication.getName(); // Get the username of the authenticated user

        MyUser user = myUserService.getUserById(id).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);
        }

        //USER can only delete his data, ADMIN can delete all data
        if (user.getUserName().equals(authenticatedUserName) || myUserService.isUserAdmin(authenticatedUserName)) {
            myUserService.deleteUser(user.getId());
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to delete this user");
        }
    }
}
