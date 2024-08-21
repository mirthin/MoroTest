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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    /**
     * Retrieves a list of users or a specific user by ID.
     *
     * @param id Optional ID of the user to retrieve.
     * @return A ResponseEntity containing the list of users or a specific user.
     */
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

    /**
     * Retrieves a specific user by ID.
     *
     * @param id ID of the user to retrieve.
     * @return A ResponseEntity containing the user or an error message.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return myUserService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves a specific user by username.
     *
     * @param username The username of the user to retrieve.
     * @return A ResponseEntity containing the user or an error message.
     */
    @GetMapping("/username")
    public ResponseEntity<?> getUser(@RequestParam(required = true)  String username) {
        return myUserService.getUserByUsername(username)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a new user.
     *
     * @param user The user details to create.
     * @return A ResponseEntity containing the created user or an error message.
     */
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
            return new ResponseEntity<>("Password has to be entered", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates an existing user's details.
     *
     * @param authorizationHeader The authorization header in format: "Authorization": "Basic" + Base64.encode("<username>:<password>")
     * @param username            The username of the user to update.
     * @param userDetails         The updated user details.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @PutMapping()
    public ResponseEntity<?>  updateUser(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                         @RequestParam(required = true) String username, @RequestBody @Valid MyUser userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        MyUser user = myUserService.getUserByUsername(username).orElse(null);
        if(user == null) {
            return new ResponseEntity<>("User not found with username: " + username, HttpStatus.NOT_FOUND);
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
            return new ResponseEntity<>("You do not have permission to update this user", HttpStatus.UNAUTHORIZED);
        }
        myUserService.updateUser(user);
        return ResponseEntity.status(HttpStatus.OK).body("User updated: " + user);
    }

    /**
     * Updates the password for an existing user.
     *
     * @param authorizationHeader The authorization header in format: "Authorization": "Basic" + Base64.encode("<username>:<password>")
     * @param username            The username of the user to update.
     * @param newPassword         The new password details.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                            @RequestParam(required = true) String username,
                                            @Valid  @RequestBody Password newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName();

        MyUser user = myUserService.getUserByUsername(username).orElse(null);
        if(user == null) {
            return new ResponseEntity<>("User not found with username: " + username, HttpStatus.NOT_FOUND);
        }

        //USER can only change his password, ADMIN can change passwords for all users
        if (user.getUsername().equals(authenticatedUsername) || myUserService.isUserAdmin(authenticatedUsername)) {
            user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
        } else {
            return new ResponseEntity<>("You do not have permission to update this user", HttpStatus.UNAUTHORIZED);
        }
        myUserService.updateUser(user);
        return new ResponseEntity<>("Password updated for user: " + user, HttpStatus.OK);
    }

    /**
     * Deletes a user by username.
     *
     * @param authorizationHeader The authorization header in format: "Authorization": "Basic" + Base64.encode("<username>:<password>")
     * @param username            The username of the user to delete.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader(value = "Authorization", required = true) String authorizationHeader,
                                        @RequestParam(required = true) String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = authentication.getName(); // Get the username of the authenticated user

        MyUser user = myUserService.getUserByUsername(username).orElse(null);
        if(user == null) {
            return new ResponseEntity<>("User not found with username:"  + username , HttpStatus.NOT_FOUND);
        }

        //USER can only delete his data, ADMIN can delete all data
        if (user.getUsername().equals(authenticatedUsername) || myUserService.isUserAdmin(authenticatedUsername)) {
            myUserService.deleteUser(user.getId());
            return ResponseEntity.status(HttpStatus.OK).body("user successfully deleted");
        } else {
            return new ResponseEntity<>("You do not have permission to delete this user", HttpStatus.UNAUTHORIZED);
        }
    }


    /**
     * A simple test endpoint for verifying that the service is running.
     *
     * @return A ResponseEntity with a success message.
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.status(HttpStatus.OK).body("Test success");
    }


    /**
     * Deletes all users from the system. This is intended for testing purposes only.
     */
    //ONLY FOR TESTING
    @DeleteMapping("/deleteall")
    public void deleteAllUsers() {
        myUserService.deleteAllUsers();
    }
}
