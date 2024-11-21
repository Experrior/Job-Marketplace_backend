package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserProfile
import com.jobsearch.userservice.exceptions.FileSizeExceededException
import com.jobsearch.userservice.exceptions.InvalidFileTypeException
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

    companion object {
        private val VALID_PICTURE_TYPES = listOf("image/jpeg", "image/png")
        private const val MAX_PICTURE_SIZE = 2 * 1024 * 1024
    }

    override fun getProfileByUserId(userId: UUID): UserProfileResponse {
        val userProfile = getUserProfileEntityByUserId(userId)

        return mapper.toUserProfileResponse(userProfile)
    }

    override fun getAllProfiles(limit: Int, offset: Int): List<UserProfile> {
        return userProfileRepository.findAll()
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

        checkFileType(profilePicture, VALID_PICTURE_TYPES)
        checkFileSize(profilePicture, MAX_PICTURE_SIZE)
        profile.s3ProfilePicturePath?.let { fileStorageService.deleteFile(it) }
        profile.s3ProfilePicturePath = fileStorageService.storeProfilePicture(userId, profilePicture)

        val savedProfile = userProfileRepository.save(profile)
        return ProfilePictureResponse(
            profilePictureUrl = fileStorageService.getFileUrl(savedProfile.s3ProfilePicturePath!!)
        )
    }

    override fun getUserProfileEntityByUserId(userId: UUID): UserProfile {
        val user = userService.getUserById(userId)
        return userProfileRepository.findByUser(user)
            ?: throw ProfileNotFoundException("Profile not found for user with id: $userId")
    }

    private fun checkFileType(file: MultipartFile, validTypes: List<String>) {
        if (file.contentType !in validTypes) {
            throw InvalidFileTypeException(
                "Expected file types: ${validTypes.joinToString(", ")}, but got: ${file.contentType}")
        }
    }

    private fun checkFileSize(file: MultipartFile, maxSize: Int) {
        if (file.size > maxSize) {
            throw FileSizeExceededException("File size exceeds the maximum limit of $maxSize bytes")
        }
    }
}