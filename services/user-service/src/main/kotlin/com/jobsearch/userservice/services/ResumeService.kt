package com.jobsearch.userservice.services

import com.jobsearch.userservice.responses.ResumeResponse
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface ResumeService {
    fun addResume(userId: UUID, resume: MultipartFile): List<ResumeResponse>
    fun deleteResume(userId: UUID, resumeId: UUID): List<ResumeResponse>
    fun userResumes(userId: UUID): List<ResumeResponse>
}