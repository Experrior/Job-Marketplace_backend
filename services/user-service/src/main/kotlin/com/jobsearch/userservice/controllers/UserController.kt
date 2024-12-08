package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.services.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class UserController(private val userService: UserService) {
    @PreAuthorize("hasRole('RECRUITER')")
    @QueryMapping
    fun userById(@Argument userId: UUID): User?{
        return userService.getUserById(userId)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    fun allUsers(@Argument limit: Int?, @Argument offset: Int?): List<User> {
        return userService.getAllUsers(limit ?: 10, offset ?: 0)
    }

    @QueryMapping
    fun recruiterCompany(@AuthenticationPrincipal recruiterId: UUID): UUID? {
        val user = userService.getUserById(recruiterId)
        return user.companyId
    }

    @QueryMapping
    fun userFullName(@Argument userId: UUID): String {
        val user = userService.getUserById(userId)
        return "${user.firstName} ${user.lastName}"
    }
}