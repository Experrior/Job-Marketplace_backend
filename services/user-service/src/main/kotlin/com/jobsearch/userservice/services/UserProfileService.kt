package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserProfile
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface UserProfileService {
    fun getProfileByUserId(userId: UUID): UserProfile
    fun getAllProfiles(limit: Int = 10, offset: Int = 0): List<UserProfile>
    fun createProfile(userId: UUID, resume: MultipartFile, profilePicture: MultipartFile): UserProfile
    fun updateUserProfile(userId: UUID, resume: MultipartFile?, profilePicture: MultipartFile?): UserProfile
    fun getProfileById(profileId: UUID): UserProfile
    fun deleteProfileByUserId(userId: UUID): Boolean
}