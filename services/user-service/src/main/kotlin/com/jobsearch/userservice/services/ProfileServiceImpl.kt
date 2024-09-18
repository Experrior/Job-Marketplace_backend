package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Profile
import com.jobsearch.userservice.repositories.ProfileRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository
) : ProfileService{
    override fun getProfileByUserId(userId: UUID): Profile? {
        return profileRepository.findById(userId).orElse(null)
    }

    override fun getAllProfiles(limit: Int, offset: Int): List<Profile> {
        return profileRepository.findAll();
    }

    override fun createProfile() {
        TODO("Not yet implemented")
    }
}