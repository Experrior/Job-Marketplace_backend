package com.jobsearch.userservice.services

import org.springframework.web.multipart.MultipartFile
import java.util.*

interface FileStorageService {
    fun storeProfilePicture(userId: UUID, profilePicture: MultipartFile): String
    fun storeResume(userId: UUID, resume: MultipartFile): String
    fun storeCompanyLogo(companyId: UUID, logo: MultipartFile): String
    fun getFileUrl(s3FilePath: String): String
}