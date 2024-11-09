package com.jobsearch.jobservice.services

import org.springframework.web.multipart.MultipartFile
import java.util.*

interface ResumeStorageService {
    fun storeResume(userId: UUID, jobId: UUID, resume: MultipartFile): String
    fun getResumeUrl(s3FilePath: String): String
}