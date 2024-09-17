package com.moro.moroTestKotlin.controller

import com.moro.moroTestKotlin.annotation.CurrentUser
import com.moro.moroTestKotlin.dao.MyUser
import com.moro.moroTestKotlin.dao.Password
import com.moro.moroTestKotlin.model.UserDetailModel
import com.moro.moroTestKotlin.service.MyUserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * REST controller for managing users.
 */
@RestController
@Validated
@RequestMapping("/api/users")
class UserController () {
    @Autowired
    private lateinit var myUserService: MyUserService

    @GetMapping
    fun getUsers(): ResponseEntity<*> {
        val users: List<MyUser?> = myUserService.allUsers
        return ResponseEntity(users, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<*> {
        return myUserService.getUserById(id)
            .map { user -> ResponseEntity(user, HttpStatus.OK) }
            .orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }

    @PostMapping
    fun createUser(@RequestBody @Valid user: MyUser): ResponseEntity<*> {
        myUserService.addUser(user)
        return ResponseEntity.status(HttpStatus.OK).body(user)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == #authUser.getId()")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody @Valid newUserDetails: MyUser,
        @CurrentUser authUser: UserDetailModel
    ): ResponseEntity<*> {
        val user: MyUser = myUserService.validateAndRetrieveUser(id)
        myUserService.updateUser(user, newUserDetails)
        return ResponseEntity.status(HttpStatus.OK).body("User updated: $user")
    }

    @PutMapping("/password/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == #authUser.getId()")
    fun updatePassword(
        @PathVariable id: Long,
        @Valid @RequestBody newPassword: Password,
        @CurrentUser authUser: UserDetailModel
    ): ResponseEntity<*> {
        val user: MyUser = myUserService.validateAndRetrieveUser(id)
        myUserService.updateUserPassword(user, newPassword)
        return ResponseEntity("Password updated for user: $user", HttpStatus.OK)
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == #authUser.getId()")
    fun deleteUser(@PathVariable id: Long, @CurrentUser authUser: UserDetailModel): ResponseEntity<*> {
        val user: MyUser = myUserService.validateAndRetrieveUser(id)
        myUserService.deleteUser(user.id)
        return ResponseEntity.status(HttpStatus.OK).body("User successfully deleted")
    }
}