package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.exceptions.UserNotFoundException
import com.jobsearch.userservice.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
): UserService, UserDetailsService{
    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override fun getUserById(userId: UUID): User {
        return userRepository.findById(userId)
            .orElseThrow {UserNotFoundException("User with ID $userId not found")}
    }

    override fun getAllUsers(limit: Int, offset: Int): List<User> {
        return userRepository.findAll();
    }

    override fun save(user: User): User {
        return userRepository.save(user)
    }

    override fun saveAll(users: List<User>) {
        userRepository.saveAll(users)
    }

    override fun isUserEligibleForProfile(user: User): Boolean {
        return user.isEmailVerified && user.isEnabled
    }

    override fun existsByUserId(userId: UUID): Boolean {
        return userRepository.existsById(userId)
    }

    override fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findUserDetailsByEmail(username)
            ?: throw UsernameNotFoundException("Email not found: $username")
    }

    override fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw UserNotFoundException("Email not found: $email")
    }
}