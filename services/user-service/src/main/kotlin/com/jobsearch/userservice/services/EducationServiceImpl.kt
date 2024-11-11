package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Education
import com.jobsearch.userservice.entities.UserProfile
import com.jobsearch.userservice.exceptions.EducationNotFoundException
import com.jobsearch.userservice.repositories.EducationRepository
import com.jobsearch.userservice.requests.EducationRequest
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class EducationServiceImpl(
    private val educationRepository: EducationRepository,
    private val userProfileService: UserProfileService
): EducationService {
    override fun getEducationByUserProfile(userId: UUID): List<Education> {
        val profile = getUserProfile(userId)
        return educationRepository.findByUserProfile(profile)
    }

    @Transactional
    override fun createEducation(userId: UUID, educationRequest: EducationRequest): Education {
        val profile = getUserProfile(userId)

        val education = Education(
            userProfile = profile,
            institutionName = educationRequest.institutionName,
            degree = educationRequest.degree,
            startDate = educationRequest.startDate,
            endDate = educationRequest.endDate,
        )

        return educationRepository.save(education)
    }

    @Transactional
    override fun updateEducation(userId: UUID, educationId: UUID, educationRequest: EducationRequest): Education {
        val education = getEducationById(userId, educationId)

        education.institutionName = educationRequest.institutionName
        education.degree = educationRequest.degree
        education.startDate = educationRequest.startDate
        education.endDate = educationRequest.endDate

        return educationRepository.save(education)
    }

    override fun getEducationById(userId: UUID, educationId: UUID): Education {
        val education = educationRepository.findById(educationId)
            .orElseThrow { EducationNotFoundException("Education not found for id: $educationId") }

        checkEducationBelongsToUser(userId, education)

        return education
    }

    @Transactional
    override fun deleteEducationById(userId: UUID, educationId: UUID): Boolean {
        try {
            val education = getEducationById(userId, educationId)
            educationRepository.delete(education)
            return true
        }catch (e: EducationNotFoundException){
            return false
        }
    }

    override fun deleteAllUserEducations(userId: UUID): Boolean {
        val profile = getUserProfile(userId)
        val educations = educationRepository.findByUserProfile(profile)

        if (educations.isEmpty()) {
            return false
        }

        educationRepository.deleteAll(educations)
        return true
    }

    private fun getUserProfile(userId: UUID): UserProfile {
        return userProfileService.getProfileByUserId(userId)
    }

    private fun checkEducationBelongsToUser(userId: UUID, education: Education) {
        if (education.userProfile.user.userId != userId) {
            throw EducationNotFoundException("Education not found for id: ${education.educationId}")
        }
    }

}