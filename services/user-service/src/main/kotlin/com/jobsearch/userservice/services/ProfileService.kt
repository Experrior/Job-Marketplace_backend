package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Profile
import java.util.*

interface ProfileService {
    fun getProfileByUserId(userId: UUID): Profile?
    fun getAllProfiles(limit: Int = 10, offset: Int = 0): List<Profile>
    fun createProfile()
}