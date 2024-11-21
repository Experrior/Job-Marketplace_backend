package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.*
import com.jobsearch.userservice.responses.*
import org.springframework.stereotype.Component

@Component
class UserProfileMapper(private val fileStorageService: FileStorageService) {

    fun toResumeResponse(resume: Resume): ResumeResponse {
        return ResumeResponse(
            resumeId = resume.resumeId!!,
            resumeName = resume.resumeName,
            resumeUrl = fileStorageService.getFileUrl(resume.s3ResumePath!!),
            createdAt = resume.createdAt
        )
    }

    fun toEducationResponse(education: Education): EducationResponse {
        return EducationResponse(
            educationId = education.educationId!!,
            institutionName = education.institutionName,
            degree = education.degree,
            startDate = education.startDate,
            endDate = education.endDate
        )
    }

    fun toSkillResponse(skill: Skill): SkillResponse {
        return SkillResponse(
            skillId = skill.skillId!!,
            skillName = skill.skillName,
            proficiencyLevel = skill.proficiencyLevel
        )
    }

    fun toExperienceResponse(experience: Experience): ExperienceResponse {
        return ExperienceResponse(
            experienceId = experience.experienceId!!,
            companyName = experience.companyName,
            role = experience.role,
            startDate = experience.startDate,
            endDate = experience.endDate
        )
    }

    fun toUserProfileResponse(userProfile: UserProfile): UserProfileResponse {
        return UserProfileResponse(
            profileId = userProfile.profileId!!,
            user = userProfile.user,
            profilePictureUrl = userProfile.s3ProfilePicturePath?.let { fileStorageService.getFileUrl(it) } ?: "",
            resumes = userProfile.resumes.map { toResumeResponse(it) },
            skills = userProfile.skills.map { toSkillResponse(it) },
            experiences = userProfile.experience.map { toExperienceResponse(it) },
            educations = userProfile.education.map { toEducationResponse(it) },
            updatedAt = userProfile.updatedAt
        )
    }
}
