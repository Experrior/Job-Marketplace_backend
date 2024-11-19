package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.UserProfile
import com.jobsearch.userservice.services.UserProfileService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Controller
@RequestMapping("/user-profile")
class UserProfileController(private val userProfileService: UserProfileService) {

    @QueryMapping
    fun currentUserProfile(
        @AuthenticationPrincipal userId: UUID
        ): UserProfile? {
        return userProfileService.getProfileByUserId(userId)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @QueryMapping
    fun allUserProfiles(@Argument limit: Int? = 10, @Argument offset: Int? = 0): List<UserProfile> {
        return userProfileService.getAllProfiles(limit ?: 10, offset ?: 0)
    }

    @PostMapping("/createUserProfile", consumes = ["multipart/form-data"])
    fun createUserProfile(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam("resume", required = false) resume: MultipartFile,
        @RequestParam("profilePicture", required = false) profilePicture: MultipartFile
    ): ResponseEntity<UserProfile>{
        return ResponseEntity(userProfileService.createProfile(userId, resume, profilePicture), HttpStatus.CREATED)
    }

    @PostMapping
    fun updateCurrentUserProfile(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam("resume", required = false) resume: MultipartFile,
        @RequestParam("profilePicture", required = false) profilePicture: MultipartFile
    ): ResponseEntity<UserProfile> {
        return ResponseEntity(userProfileService.updateUserProfile(userId, resume, profilePicture), HttpStatus.OK)
    }

    @MutationMapping
    fun deleteCurrentProfile(
        @AuthenticationPrincipal userId: UUID
    ): Boolean {
        return userProfileService.deleteProfileByUserId(userId)
    }
}