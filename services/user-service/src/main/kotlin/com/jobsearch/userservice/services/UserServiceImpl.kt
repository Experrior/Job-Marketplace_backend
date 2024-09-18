package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
): UserService{
    override fun getUserById(userId: UUID): User? {
        return userRepository.findById(userId).orElse(null)
    }

    override fun getAllUsers(limit: Int, offset: Int): List<User> {
        return userRepository.findAll();
    }

    override fun registerUser() {
        TODO("Not yet implemented")
    }

}