package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserProfile
import com.jobsearch.userservice.responses.ProfilePictureResponse
import com.jobsearch.userservice.responses.UserProfileResponse
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface UserProfileService {
    fun getProfileByUserId(userId: UUID): UserProfileResponse
    fun getUserProfileEntity(userId: UUID): UserProfile
    fun getAllProfiles(limit: Int = 10, offset: Int = 0): List<UserProfile>
    fun createDefaultProfile(userId: UUID): UserProfile
//    fun updateUserProfile(userId: UUID, resume: MultipartFile?, profilePicture: MultipartFile?): UserProfile
    fun getProfileById(profileId: UUID): UserProfileResponse
    fun deleteProfileByUserId(userId: UUID): Boolean
    fun updateProfilePicture(userId: UUID, profilePicture: MultipartFile): ProfilePictureResponse
}