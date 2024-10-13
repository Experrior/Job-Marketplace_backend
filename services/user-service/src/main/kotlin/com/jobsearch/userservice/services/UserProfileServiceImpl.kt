package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserProfile
import com.jobsearch.userservice.exceptions.ProfileAlreadyExistsException
import com.jobsearch.userservice.exceptions.ProfileNotFoundException
import com.jobsearch.userservice.exceptions.UserNotEligibleForProfileException
import com.jobsearch.userservice.repositories.UserProfileRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserProfileServiceImpl(
    private val userProfileRepository: UserProfileRepository,
    private val userService: UserService
) : UserProfileService{
    override fun getProfileByKcUserId(kcUserId: String): UserProfile? {
        val user = userService.findByKeycloakUserId(kcUserId)
        return userProfileRepository.findByUser(user)
            ?: throw ProfileNotFoundException("Profile not found for user with keycloak id: $kcUserId")
    }

    override fun getAllProfiles(limit: Int, offset: Int): List<UserProfile> {
        return userProfileRepository.findAll();
    }

    override fun createProfile(resumePath: String, profilePicturePath: String): UserProfile {
        val userId = userService.getUserIdFromAuthentication()
        val user = userService.getUserById(userId)

        if(!userService.isUserEligibleForProfile(user))
            throw UserNotEligibleForProfileException(userId)

        if(userProfileRepository.existsByUser(user))
            throw ProfileAlreadyExistsException(userId)

        val userProfile = UserProfile(
            user = user,
            resumePath = resumePath,
            profilePicturePath = profilePicturePath
        )

        return userProfileRepository.save(userProfile)
    }

    override fun updateUserProfile(profileId: UUID, resumePath: String?, profilePicturePath: String?): UserProfile {
        val profile = getProfileById(profileId)
        resumePath?.let { profile.resumePath = it }
        profilePicturePath?.let { profile.profilePicturePath = it }

        return userProfileRepository.save(profile)
    }

    override fun getProfileById(profileId: UUID): UserProfile {
        return userProfileRepository.findById(profileId)
            .orElseThrow {ProfileNotFoundException("Profile not found by id: $profileId")}
    }

    override fun deleteProfileById(profileId: UUID): Boolean {
        return try {
            val profile = getProfileById(profileId)
            userProfileRepository.delete(profile)
            true
        } catch (e: ProfileNotFoundException) {
            false
        }
    }
}