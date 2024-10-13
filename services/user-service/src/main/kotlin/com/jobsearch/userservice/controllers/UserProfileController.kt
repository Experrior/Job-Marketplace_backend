package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.UserProfile
import com.jobsearch.userservice.services.UserProfileService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class UserProfileController(private val userProfileService: UserProfileService) {

    @QueryMapping
    fun currentUserProfile(@AuthenticationPrincipal principal: Jwt): UserProfile? {
        val userId = UUID.fromString(principal.subject)
        return userProfileService.getProfileByUserId(userId)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    fun allUserProfiles(@Argument limit: Int? = 10, @Argument offset: Int? = 0): List<UserProfile> {
        return userProfileService.getAllProfiles(limit ?: 10, offset ?: 0)
    }

    @Secured
    @MutationMapping
    fun createUserProfile(
        @AuthenticationPrincipal principal: Jwt,
        @Argument resumePath: String,
        @Argument profilePicturePath: String
    ): UserProfile? {
        val userId = UUID.fromString(principal.subject)
        return userProfileService.createProfile(userId, resumePath, profilePicturePath)
    }

    @MutationMapping
    fun updateCurrentUserProfile(
        @AuthenticationPrincipal principal: Jwt,
        @Argument resumePath: String? = null,
        @Argument profilePicturePath: String? = null
    ): UserProfile? {
        val userId = UUID.fromString(principal.subject)
        return userProfileService.updateUserProfile(userId, resumePath, profilePicturePath)
    }

    @MutationMapping
    fun deleteCurrentProfile(@AuthenticationPrincipal principal: Jwt): Boolean {
        val userId = UUID.fromString(principal.subject)
        return userProfileService.deleteProfileByUserId(userId)
    }
}