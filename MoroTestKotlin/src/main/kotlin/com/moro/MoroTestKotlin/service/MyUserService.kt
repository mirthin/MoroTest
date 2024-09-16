package com.moro.MoroTestKotlin.service


import com.moro.MoroTestKotlin.dao.MyUser
import com.moro.MoroTestKotlin.dao.Password
import com.moro.MoroTestKotlin.exception.BadRequestException
import com.moro.MoroTestKotlin.exception.UserNotFoundException
import com.moro.MoroTestKotlin.model.Role
import com.moro.MoroTestKotlin.model.UserDetailModel
import com.moro.MoroTestKotlin.repository.UserRepository
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
class MyUserService (private val passwordEncoder: PasswordEncoder,
                     private val userRepository: UserRepository) : UserDetailsService {


    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user: Optional<MyUser>? = userRepository.findByUsername(username)
        if(user != null){
            return user.map<UserDetails>(Function<MyUser, UserDetails> { UserDetailModel(it) }).orElseThrow<UsernameNotFoundException>(
                Supplier<UsernameNotFoundException> { UsernameNotFoundException("Invalid Username") })
        } else {
            throw UsernameNotFoundException("Invalid username")
        }

    }

    @Throws(UsernameNotFoundException::class)
    fun getUserByUsername(username: String): Optional<MyUser> {
        return userRepository.findByUsername(username)
    }

    val allUsers: List<MyUser?>
        get() = userRepository.findAll()

    fun getUserById(id: Long): Optional<MyUser?> {
        return userRepository.findById(id)
    }

    fun addUser(user: MyUser) {
        if (user.password.isEmpty()) {
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
        if (!newUserDetails.password.isEmpty()) {
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
        if(userOpt!= null) {
            return userOpt.map<Boolean>(Function<MyUser, Boolean> { user: MyUser -> user.role === Role.ROLE_ADMIN })
                .orElse(false)
        } else {
            return false
        }
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