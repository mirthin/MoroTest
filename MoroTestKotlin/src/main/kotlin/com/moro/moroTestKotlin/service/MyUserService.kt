package com.moro.moroTestKotlin.service


import com.moro.moroTestKotlin.dao.MyUser
import com.moro.moroTestKotlin.dao.Password
import com.moro.moroTestKotlin.exception.BadRequestException
import com.moro.moroTestKotlin.exception.UserAlreadyExistsException
import com.moro.moroTestKotlin.exception.UserNotFoundException
import com.moro.moroTestKotlin.model.Role
import com.moro.moroTestKotlin.model.UserDetailModel
import com.moro.moroTestKotlin.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

@Service
class MyUserService (private val passwordEncoder: PasswordEncoder) : UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user: Optional<MyUser> = userRepository.findByUsername(username)
        return user.map<UserDetails>(Function<MyUser, UserDetails> { UserDetailModel(it) }).orElseThrow<UsernameNotFoundException>(
            Supplier<UsernameNotFoundException> { UsernameNotFoundException("Invalid Username") })
    }

    val allUsers: List<MyUser?>
        get() = userRepository.findAll()

    fun getUserById(id: Long): Optional<MyUser?> {
        return userRepository.findById(id)
    }

    fun addUser(user: MyUser) {
        if (userRepository.findByUsername(user.username).isPresent) {
            throw UserAlreadyExistsException(user.username)
        }
        if (user.password.isNullOrEmpty()) {
            throw BadRequestException("Password")
        }
        user.password = (passwordEncoder.encode(user.password))
        // Assign role to first user as ADMIN, others as USER
        assignRoleToUser(user)
        userRepository.save(user)
    }

    fun updateUser(oldUserDetails: MyUser, newUserDetails: MyUser) {
        oldUserDetails.name = newUserDetails.name
        oldUserDetails.username = newUserDetails.username

        // Optionally update the password if provided
        if (!newUserDetails.password.isNullOrEmpty()) {
            oldUserDetails.password = passwordEncoder.encode(newUserDetails.password)
        }

        // Only admin can change roles
        if (isUserAdmin(SecurityContextHolder.getContext().authentication.name)) {
                oldUserDetails.role = newUserDetails.role
        }

        userRepository.save(oldUserDetails)
    }

    fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }

    fun deleteAllUsers() {
        userRepository.deleteAll()
    }

    fun isUserAdmin(username: String): Boolean {
        val userOpt: Optional<MyUser> = userRepository.findByUsername(username)
        return userOpt.map<Boolean>(Function<MyUser, Boolean> { user: MyUser -> user.role === Role.ROLE_ADMIN })
            .orElse(false)
    }

    fun validateAndRetrieveUser(id: Long): MyUser {
        return getUserById(id).get() ?: throw  UserNotFoundException(id)
    }

    fun assignRoleToUser(user: MyUser) {
        if (allUsers.isEmpty()) {
            user.role = Role.ROLE_ADMIN
        } else {
            user.role = Role.ROLE_USER
        }
    }

    fun updateUserPassword(user: MyUser, newPassword: Password) {
        user.password = passwordEncoder.encode(newPassword.password)
        userRepository.save(user)
    }
}