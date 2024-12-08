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
            s3ResumePath = resume.s3ResumePath!!,
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

    fun toLinkResponse(link: UserLink): LinkResponse {
        return LinkResponse(
            linkId = link.linkId!!,
            name = link.name,
            url = link.url
        )
    }

    fun toUserProfileResponse(userProfile: UserProfile): UserProfileResponse {
        return UserProfileResponse(
            profileId = userProfile.profileId!!,
            user = userProfile.user,
            profilePictureUrl = userProfile.s3ProfilePicturePath?.let { fileStorageService.getFileCachedUrl(it) },
            resumes = userProfile.resumes.map { toResumeResponse(it) },
            skills = userProfile.skills.map { toSkillResponse(it) },
            experiences = userProfile.experiences.map { toExperienceResponse(it) },
            educations = userProfile.educations.map { toEducationResponse(it) },
            links = userProfile.links.map { toLinkResponse(it) },
            createdAt = userProfile.createdAt,
            updatedAt = userProfile.updatedAt
        )
    }
}
