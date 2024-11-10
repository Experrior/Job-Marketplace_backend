package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.responses.QuizResponse
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface FileStorageService {
    fun storeResume(userId: UUID, jobId: UUID, resume: MultipartFile): String
    fun getFileUrl(s3FilePath: String): String
    fun storeQuizConfig(recruiterId: UUID, quizConfig: MultipartFile): String
    fun listQuizConfigs(recruiterId: UUID): List<QuizResponse>
}