package com.moro.MoroTest.controller;

import com.moro.MoroTest.dao.MyUser;
import com.moro.MoroTest.dao.Password;
import com.moro.MoroTest.model.Role;
import com.moro.MoroTest.service.MyUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping()
    public ResponseEntity<?>  updateUser(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @RequestParam(required = true) String username, @RequestBody @Valid MyUser userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        MyUser user = myUserService.getUserByUsername(username).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
        }

        //USER can only update his data, ADMIN can change data for all users
        if (user.getUsername().equals(authenticatedUsername) || myUserService.isUserAdmin(authenticatedUsername)) {
            user.setName(userDetails.getName());
            user.setUsername(userDetails.getUsername());

            // Optionally update the password if it is provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            //only admin can change roles
            if(myUserService.isUserAdmin(authenticatedUsername)) {
                user.setRole(userDetails.getRole());
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to delete this user");
        }
        myUserService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body("User updated: " + user);
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                            @RequestParam(required = true) String username, @Valid  @RequestBody Password newPassword,
                                            BindingResult bindingResult) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        MyUser user = myUserService.getUserByUsername(username).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
        }

        //USER can only change his password, ADMIN can change passwords for all users
        if (user.getUsername().equals(authenticatedUsername) || myUserService.isUserAdmin(authenticatedUsername)) {
            user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to delete this user");
        }
        myUserService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body("Password updated for user: " + user);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader(value = "Authorization", required = true) String authorizationHeader, @RequestParam(required = true) String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName(); // Get the username of the authenticated user

        MyUser user = myUserService.getUserByUsername(username).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
        }

        //USER can only delete his data, ADMIN can delete all data
        if (user.getUsername().equals(authenticatedUsername) || myUserService.isUserAdmin(authenticatedUsername)) {
            myUserService.deleteUser(user.getId());
            return ResponseEntity.status(HttpStatus.OK).body("user successfully deleted");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You do not have permission to delete this user");
        }
    }


    //ONLY FOR TESTING
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.status(HttpStatus.OK).body("Test success");
    }


    //ONLY FOR TESTING
    @DeleteMapping("/deleteall")
    public void deleteAllUsers() {
        myUserService.deleteAllUsers();
    }
}
