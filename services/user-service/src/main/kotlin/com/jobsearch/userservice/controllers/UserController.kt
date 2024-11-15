package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.services.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class UserController(private val userService: UserService) {
    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    fun userById(@Argument userId: UUID): User?{
        return userService.getUserById(userId)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    fun allUsers(@Argument limit: Int?, @Argument offset: Int?): List<User> {
        return userService.getAllUsers(limit ?: 10, offset ?: 0)
    }
}