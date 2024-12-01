package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Experience
import com.jobsearch.userservice.exceptions.ExperienceNotFoundException
import com.jobsearch.userservice.exceptions.UnauthorizedAccessException
import com.jobsearch.userservice.repositories.ExperienceRepository
import com.jobsearch.userservice.requests.ExperienceRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.ExperienceResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class ExperienceServiceImpl(
    private val experienceRepository: ExperienceRepository,
    private val userProfileService: UserProfileService,
    private val mapper: UserProfileMapper
): ExperienceService {

    @Transactional
    override fun addExperience(userId: UUID, experienceRequest: ExperienceRequest): ExperienceResponse {
        val userProfile = userProfileService.getUserProfileEntityByUserId(userId)

        val newExperience = Experience(
            userProfile = userProfile,
            companyName = experienceRequest.companyName,
            role = experienceRequest.role,
            startDate = experienceRequest.startDate,
            endDate = experienceRequest.endDate
        )

        return mapper.toExperienceResponse(experienceRepository.save(newExperience))
    }

    @Transactional
    override fun deleteExperienceById(userId: UUID, experienceId: UUID): DeleteResponse{
        val experience = getExperienceEntity(experienceId)

        checkExperienceBelongsToUser(userId, experience)

        experienceRepository.delete(experience)

        return DeleteResponse(
            success = true,
            message = "Experience deleted successfully"
        )
    }

    @Transactional
    override fun deleteAllUserExperiences(userId: UUID): DeleteResponse {
        val userProfile = userProfileService.getUserProfileEntityByUserId(userId)
        val experiences = experienceRepository.findByUserProfile(userProfile)

        return try {
            experienceRepository.deleteAll(experiences)
            DeleteResponse(
                success = true,
                message = "All experiences deleted successfully"
            )
        } catch (e: Exception) {
            DeleteResponse(
                success = false,
                message = "Error deleting experiences"
            )
        }
    }

    private fun getExperienceEntity(experienceId: UUID): Experience {
        return experienceRepository.findById(experienceId)
            .orElseThrow { ExperienceNotFoundException("Experience not found by id: $experienceId") }
    }

    private fun checkExperienceBelongsToUser(userId: UUID, experience: Experience) {
        if (experience.userProfile.user.userId != userId) {
            throw UnauthorizedAccessException("User is not authorized to access this experience.")
        }
    }
}