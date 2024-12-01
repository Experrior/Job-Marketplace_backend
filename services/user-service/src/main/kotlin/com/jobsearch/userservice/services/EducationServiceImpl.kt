package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Education
import com.jobsearch.userservice.exceptions.EducationNotFoundException
import com.jobsearch.userservice.exceptions.UnauthorizedAccessException
import com.jobsearch.userservice.repositories.EducationRepository
import com.jobsearch.userservice.requests.EducationRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.EducationResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class EducationServiceImpl(
    private val educationRepository: EducationRepository,
    private val userProfileService: UserProfileService,
    private val mapper: UserProfileMapper
): EducationService {
    override fun getEducationsByUserProfile(userId: UUID): List<EducationResponse> {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)

        return educationRepository.findByUserProfile(profile).map { mapper.toEducationResponse(it) }
    }

    @Transactional
    override fun addEducation(userId: UUID, educationRequest: EducationRequest): EducationResponse {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)

        val education = Education(
            userProfile = profile,
            institutionName = educationRequest.institutionName,
            degree = educationRequest.degree,
            startDate = educationRequest.startDate,
            endDate = educationRequest.endDate,
        )

        return mapper.toEducationResponse(educationRepository.save(education))
    }

    @Transactional
    override fun updateEducation(userId: UUID, educationId: UUID, educationRequest: EducationRequest): EducationResponse {
        val education = getEducationEntity(educationId)
        checkEducationBelongsToUser(userId, education)

        education.institutionName = educationRequest.institutionName
        education.degree = educationRequest.degree
        education.startDate = educationRequest.startDate
        education.endDate = educationRequest.endDate

        return mapper.toEducationResponse(educationRepository.save(education))
    }

    override fun getEducationById(userId: UUID, educationId: UUID): EducationResponse {
        val education = getEducationEntity(educationId)

        checkEducationBelongsToUser(userId, education)

        return mapper.toEducationResponse(education)
    }

    @Transactional
    override fun deleteEducationById(userId: UUID, educationId: UUID): DeleteResponse {
        val education = getEducationEntity(educationId)
        checkEducationBelongsToUser(userId, education)
        educationRepository.delete(education)

        return DeleteResponse(
            success = true,
            message = "Education deleted successfully"
        )
    }

    override fun deleteAllUserEducations(userId: UUID): DeleteResponse {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)
        val educations = educationRepository.findByUserProfile(profile)

        if (educations.isEmpty()) {
            return DeleteResponse(
                success = false,
                message = "No educations found for the user"
            )
        }

        educationRepository.deleteAll(educations)
        return DeleteResponse(
            success = true,
            message = "All educations deleted successfully"
        )
    }

    private fun getEducationEntity(educationId: UUID): Education {
        return educationRepository.findById(educationId)
            .orElseThrow { EducationNotFoundException("Education not found for id: $educationId") }
    }


    private fun checkEducationBelongsToUser(userId: UUID, education: Education) {
        if (education.userProfile.user.userId != userId) {
            throw UnauthorizedAccessException("Education does not belong to the user's profile")
        }
    }

}