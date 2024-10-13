package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserProfile
import java.util.*

interface UserProfileService {
    fun getProfileByKcUserId(kcUserId: String): UserProfile?
    fun getAllProfiles(limit: Int = 10, offset: Int = 0): List<UserProfile>
    fun createProfile(resumePath: String, profilePicturePath: String): UserProfile
    fun updateUserProfile(profileId: UUID, resumePath: String?, profilePicturePath: String?): UserProfile
    fun getProfileById(profileId: UUID): UserProfile
    fun deleteProfileById(profileId: UUID): Boolean
}