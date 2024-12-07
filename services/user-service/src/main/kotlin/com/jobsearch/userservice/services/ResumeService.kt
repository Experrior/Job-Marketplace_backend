package com.jobsearch.userservice.services

import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.ResumeResponse
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface ResumeService {
    fun addResume(userId: UUID, resume: MultipartFile): ResumeResponse
    fun deleteResume(userId: UUID, resumeId: UUID): DeleteResponse
    fun userResumes(userId: UUID): List<ResumeResponse>
    fun getResumeById(userId: UUID, resumeId: UUID): ResumeResponse
}