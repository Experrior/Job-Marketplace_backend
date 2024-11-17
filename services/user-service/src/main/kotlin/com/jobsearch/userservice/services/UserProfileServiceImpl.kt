package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserProfile
import com.jobsearch.userservice.exceptions.*
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
    override fun getProfileByUserId(userId: UUID): UserProfile {
        val user = userService.getUserById(userId)
        return userProfileRepository.findByUser(user)
            ?: throw ProfileNotFoundException("Profile not found for user with id: $userId")
    }

    override fun getAllProfiles(limit: Int, offset: Int): List<UserProfile> {
        return userProfileRepository.findAll();
    }

    override fun createProfile(userId: UUID, resume: MultipartFile, profilePicture: MultipartFile): UserProfile {
        val user = userService.getUserById(userId)

        if(!userService.isUserEligibleForProfile(user))
            throw UserNotEligibleForProfileException(userId)

        if(userProfileRepository.existsByUser(user))
            throw ProfileAlreadyExistsException(userId)


        checkFileType(resume, listOf("application/pdf"))
        checkFileType(profilePicture, listOf("image/jpeg", "image/png"))
        checkFileSize(resume, 2 * 1024 * 1024) // 2 MB
        checkFileSize(profilePicture, 1 * 1024 * 1024) // 1 MB


        val resumePath = fileStorageService.storeResume(userId, resume)
        val profilePicturePath = fileStorageService.storeProfilePicture(userId, profilePicture)

        val userProfile = UserProfile(
            user = user,
            s3ResumePath = resumePath,
            s3ProfilePicturePath = profilePicturePath
        )

        return userProfileRepository.save(userProfile)
    }

    override fun updateUserProfile(userId: UUID, resume: MultipartFile?, profilePicture: MultipartFile?): UserProfile {
        val profile = getProfileByUserId(userId)

        resume?.let {
            profile.s3ResumePath?.let { it1 -> fileStorageService.deleteFile(it1) }
            profile.s3ResumePath = fileStorageService.storeResume(userId, it)
        }

        profilePicture?.let {
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

    private fun checkFileSize(file: MultipartFile, maxSize: Long) {
        if (file.size > maxSize) {
            throw FileSizeExceededException("File size exceeds the maximum limit of $maxSize bytes")
        }
    }
}