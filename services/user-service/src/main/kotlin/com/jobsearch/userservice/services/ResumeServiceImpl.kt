package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Resume
import com.jobsearch.userservice.entities.UserRole
import com.jobsearch.userservice.exceptions.ResumeNotFoundException
import com.jobsearch.userservice.exceptions.UnauthorizedAccessException
import com.jobsearch.userservice.repositories.ResumeRepository
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.ResumeResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ResumeServiceImpl(
    private val resumeRepository: ResumeRepository,
    private val userProfileService: UserProfileService,
    private val fileStorageService: FileStorageService,
    private val mapper: UserProfileMapper
): ResumeService {

    @Transactional
    override fun addResume(userId: UUID, resume: MultipartFile): ResumeResponse {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)

        val resumePath = fileStorageService.storeResume(userId, resume)
        val newResume = Resume(
            userProfile = profile,
            resumeName = resume.originalFilename ?: "Unnamed",
            s3ResumePath = resumePath
        )

        return mapper.toResumeResponse(resumeRepository.save(newResume))
    }

    @Transactional
    override fun deleteResume(userId: UUID, resumeId: UUID): DeleteResponse {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)
        val resumeToRemove = resumeRepository.findById(resumeId)
            .orElseThrow { ResumeNotFoundException("Resume not found with id: $resumeId") }

        if (resumeToRemove.userProfile != profile) {
            throw UnauthorizedAccessException("Resume does not belong to the user's profile")
        }

        resumeToRemove.s3ResumePath?.let { fileStorageService.deleteFile(it) }
        resumeRepository.delete(resumeToRemove)

        return DeleteResponse(
            success = true,
            message = "Resume deleted successfully"
        )
    }

    override fun userResumes(userId: UUID): List<ResumeResponse> {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)

        return resumeRepository.findByUserProfile(profile).map { mapper.toResumeResponse(it) }
    }

    override fun getResumeById(userId: UUID, resumeId: UUID): ResumeResponse {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)
        val resume = resumeRepository.findById(resumeId)
            .orElseThrow { ResumeNotFoundException("Resume not found with id: $resumeId") }

        if (profile.user.role != UserRole.RECRUITER && resume.userProfile != profile) {
            throw UnauthorizedAccessException("Resume does not belong to the user's profile")
        }

        return mapper.toResumeResponse(resume)
    }
}
