package com.jobsearch.jobservice.services

import org.springframework.web.multipart.MultipartFile
import java.util.*

interface FileStorageService {
    fun storeResume(userId: UUID, jobId: UUID, resume: MultipartFile): String
    fun getFileUrl(s3FilePath: String): String
    fun storeQuizConfig(recruiterId: UUID, quizConfig: MultipartFile): String
    fun updateQuizConfig(recruiterId: UUID,  quizConfig: MultipartFile): String
    fun listQuizConfigs(recruiterId: UUID): List<String>
}