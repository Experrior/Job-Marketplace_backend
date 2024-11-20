package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.*
import com.jobsearch.userservice.exceptions.FileSizeExceededException
import com.jobsearch.userservice.exceptions.InvalidFileTypeException
import com.jobsearch.userservice.exceptions.ProfileNotFoundException
import com.jobsearch.userservice.exceptions.UserNotEligibleForProfileException
import com.jobsearch.userservice.repositories.UserProfileRepository
import com.jobsearch.userservice.responses.*
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
        private val VALID_PICTURE_TYPES = listOf("image/jpeg", "image/png")
        private val VALID_RESUME_TYPES = listOf("application/pdf")
        private const val MAX_PICTURE_SIZE = 2 * 1024 * 1024
        private const val MAX_RESUME_SIZE = 2 * 1024 * 1024
    }

    override fun getProfileByUserId(userId: UUID): UserProfileResponse {
        val userProfile = getUserProfileEntity(userId)

        return convertToUserProfileResponse(userProfile)
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

        return convertToUserProfileResponse(userProfile)
    }

    override fun deleteProfileByUserId(userId: UUID): Boolean {
        return try {
            val profile = getUserProfileEntity(userId)
            userProfileRepository.delete(profile)

            true
        } catch (e: ProfileNotFoundException) {
            false
        }
    }

    override fun updateProfilePicture(userId: UUID, profilePicture: MultipartFile): ProfilePictureResponse {
        val profile = getUserProfileEntity(userId)

        checkFileType(profilePicture, VALID_PICTURE_TYPES)
        checkFileSize(profilePicture, MAX_PICTURE_SIZE)
        profile.s3ProfilePicturePath?.let { fileStorageService.deleteFile(it) }
        profile.s3ProfilePicturePath = fileStorageService.storeProfilePicture(userId, profilePicture)

        val savedProfile = userProfileRepository.save(profile)
        return ProfilePictureResponse(
            profilePictureUrl = fileStorageService.getFileUrl(savedProfile.s3ProfilePicturePath!!)
        )
    }

    override fun getUserProfileEntity(userId: UUID): UserProfile {
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

    private fun convertToResumeResponse(resume: Resume): ResumeResponse {
        return ResumeResponse(
            resumeId = resume.resumeId!!,
            resumeName = resume.resumeName,
            resumeUrl = fileStorageService.getFileUrl(resume.s3ResumePath!!),
            createdAt = resume.createdAt
        )
    }

    private fun convertToSkillResponse(skill: Skill): SkillResponse {
        return SkillResponse(
            skillId = skill.skillId!!,
            skillName = skill.skillName,
            proficiencyLevel = skill.proficiencyLevel
        )
    }

    private fun convertToExperienceResponse(experience: Experience): ExperienceResponse {
        return ExperienceResponse(
            experienceId = experience.experienceId!!,
            companyName = experience.companyName,
            role = experience.role,
            startDate = experience.startDate,
            endDate = experience.endDate
        )
    }

    private fun convertToEducationResponse(education: Education): EducationResponse {
        return EducationResponse(
            educationId = education.educationId!!,
            institutionName = education.institutionName,
            degree = education.degree,
            startDate = education.startDate,
            endDate = education.endDate
        )
    }

    private fun convertToUserProfileResponse(userProfile: UserProfile): UserProfileResponse {
        return UserProfileResponse(
            profileId = userProfile.profileId!!,
            user = userProfile.user,
            profilePictureUrl = fileStorageService.getFileUrl(userProfile.s3ProfilePicturePath!!),
            resumes = userProfile.resumes.map { resume ->
                convertToResumeResponse(resume)
            },
            skills = userProfile.skills.map { skill ->
                convertToSkillResponse(skill)
            },
            experiences = userProfile.experience.map { experience ->
                convertToExperienceResponse(experience)
            },
            educations = userProfile.education.map { education ->
                convertToEducationResponse(education)
            },
            updatedAt = userProfile.updatedAt
        )
    }
}