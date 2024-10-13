package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserProfile
import java.util.*

interface UserProfileService {
    fun getProfileByUserId(userId: UUID): UserProfile?
    fun getAllProfiles(limit: Int = 10, offset: Int = 0): List<UserProfile>
    fun createProfile(userId: UUID, resumePath: String, profilePicturePath: String): UserProfile
    fun updateUserProfile(userId: UUID, resumePath: String?, profilePicturePath: String?): UserProfile
    fun getProfileById(profileId: UUID): UserProfile
    fun deleteProfileByUserId(userId: UUID): Boolean
}