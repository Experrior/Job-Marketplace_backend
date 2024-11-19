package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserProfile
import com.jobsearch.userservice.exceptions.FileSizeExceededException
import com.jobsearch.userservice.exceptions.InvalidFileTypeException
import com.jobsearch.userservice.exceptions.ProfileNotFoundException
import com.jobsearch.userservice.exceptions.UserNotEligibleForProfileException
import com.jobsearch.userservice.repositories.UserProfileRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class UserProfileServiceImpl(
    private val userProfileRepository: UserProfileRepository,
    private val userService: UserService,
    private val fileStorageService: FileStorageService
) : UserProfileService{

    companion object {
        private val VALID_RESUME_TYPES = listOf("application/pdf")
        private val VALID_PICTURE_TYPES = listOf("image/jpeg", "image/png")
        private const val MAX_RESUME_SIZE = 2 * 1024 * 1024
        private const val MAX_PICTURE_SIZE = 2 * 1024 * 1024
    }

    override fun getProfileByUserId(userId: UUID): UserProfile {
        val user = userService.getUserById(userId)
        return userProfileRepository.findByUser(user)
            ?: throw ProfileNotFoundException("Profile not found for user with id: $userId")
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
            s3ResumePath = null,
            s3ProfilePicturePath = null
        )

        return userProfileRepository.save(userProfile)
    }

    override fun updateUserProfile(userId: UUID, resume: MultipartFile?, profilePicture: MultipartFile?): UserProfile {
        val profile = getProfileByUserId(userId)

        resume?.let {
            checkFileType(it, VALID_RESUME_TYPES)
            checkFileSize(it, MAX_RESUME_SIZE)
            profile.s3ResumePath?.let { it1 -> fileStorageService.deleteFile(it1) }
            profile.s3ResumePath = fileStorageService.storeResume(userId, it)
        }

        profilePicture?.let {
            checkFileType(it, VALID_PICTURE_TYPES)
            checkFileSize(it, MAX_PICTURE_SIZE)
            profile.s3ProfilePicturePath?.let { it1 -> fileStorageService.deleteFile(it1) }
            profile.s3ProfilePicturePath = fileStorageService.storeProfilePicture(userId, it)
        }

        return userProfileRepository.save(profile)
    }

    override fun getProfileById(profileId: UUID): UserProfile {
        return userProfileRepository.findById(profileId)
            .orElseThrow {ProfileNotFoundException("Profile not found by id: $profileId")}
    }

    override fun deleteProfileByUserId(userId: UUID): Boolean {
        return try {
            val profile = getProfileByUserId(userId)
            userProfileRepository.delete(profile)

            true
        } catch (e: ProfileNotFoundException) {
            false
        }
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