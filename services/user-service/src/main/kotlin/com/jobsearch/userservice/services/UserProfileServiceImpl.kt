package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserProfile
import com.jobsearch.userservice.exceptions.ProfileNotFoundException
import com.jobsearch.userservice.exceptions.UserNotEligibleForProfileException
import com.jobsearch.userservice.repositories.UserProfileRepository
import com.jobsearch.userservice.responses.ProfilePictureResponse
import com.jobsearch.userservice.responses.UserProfileResponse
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class UserProfileServiceImpl(
    private val userProfileRepository: UserProfileRepository,
    private val userService: UserService,
    private val fileStorageService: FileStorageService,
    private val mapper: UserProfileMapper
) : UserProfileService{

    override fun getProfileByUserId(userId: UUID): UserProfileResponse {
        val userProfile = getUserProfileEntityByUserId(userId)

        return mapper.toUserProfileResponse(userProfile)
    }

    override fun getAllProfiles(limit: Int, offset: Int): List<UserProfileResponse> {
        return userProfileRepository.findAll().map { mapper.toUserProfileResponse(it) }
    }

    override fun createDefaultProfile(userId: UUID): UserProfile {
        val user = userService.getUserById(userId)

        if(!userService.isUserEligibleForProfile(user))
            throw UserNotEligibleForProfileException(userId)

        val userProfile = UserProfile(
            user = user,
            resumes = mutableListOf(),
            s3ProfilePicturePath = null
        )

        return userProfileRepository.save(userProfile)
    }

    override fun getProfileById(profileId: UUID): UserProfileResponse {
        val userProfile = userProfileRepository.findById(profileId)
            .orElseThrow {ProfileNotFoundException("Profile not found by id: $profileId")}

        return mapper.toUserProfileResponse(userProfile)
    }

    override fun deleteProfileByUserId(userId: UUID): Boolean {
        return try {
            val profile = getUserProfileEntityByUserId(userId)
            userProfileRepository.delete(profile)
            true
        } catch (e: ProfileNotFoundException) {
            false
        }
    }

    override fun updateProfilePicture(userId: UUID, profilePicture: MultipartFile): ProfilePictureResponse {
        val profile = getUserProfileEntityByUserId(userId)
        deleteExistingProfilePicture(profile)
        val updatedProfile = updateProfilePicturePath(profile, userId, profilePicture)
        return createProfilePictureResponse(updatedProfile)
    }

    private fun deleteExistingProfilePicture(profile: UserProfile) {
        profile.s3ProfilePicturePath?.let { fileStorageService.deleteFile(it) }
    }

    private fun updateProfilePicturePath(profile: UserProfile, userId: UUID, profilePicture: MultipartFile): UserProfile {
        profile.s3ProfilePicturePath = fileStorageService.storeProfilePicture(userId, profilePicture)
        return userProfileRepository.save(profile)
    }

    private fun createProfilePictureResponse(profile: UserProfile): ProfilePictureResponse {
        return ProfilePictureResponse(
            profilePictureUrl = fileStorageService.getFileUrl(profile.s3ProfilePicturePath!!)
        )
    }

    override fun getUserProfileEntityByUserId(userId: UUID): UserProfile {
        val user = userService.getUserById(userId)
        return userProfileRepository.findByUser(user)
            ?: throw ProfileNotFoundException("Profile not found for user with id: $userId")
    }
}