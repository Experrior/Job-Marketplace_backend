package com.jobsearch.userservice.services

import com.jobsearch.userservice.exceptions.FailedToStoreFileException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration
import java.util.*

@Service
class FileStorageServiceImpl(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner
): FileStorageService {
    private val logger = LoggerFactory.getLogger(FileStorageServiceImpl::class.java)

    @Value("\${aws.s3.bucket}")
    val bucketName: String? = null

    override fun storeProfilePicture(userId: UUID, profilePicture: MultipartFile): String {
        val filePath = "profile_pictures/${userId}_${profilePicture.originalFilename}"

        return saveFile(filePath, profilePicture)
    }

    override fun storeResume(userId: UUID, resume: MultipartFile): String {
        val filePath = "resumes/$${userId}_${resume.originalFilename}"

        return saveFile(filePath, resume)
    }

    override fun storeCompanyLogo(companyId: UUID, logo: MultipartFile): String {
        val filePath = "company_logos/${companyId}_${logo.originalFilename}"

        return saveFile(filePath, logo)
    }

    override fun getFileUrl(s3FilePath: String): String {
        val presignRequest = generatePreSignedUrlRequest(bucketName!!, s3FilePath)
        val presignedURL = s3Presigner.presignGetObject(presignRequest)

        return presignedURL.url().toString()
    }

    private fun saveFile(filePath: String, file: MultipartFile): String {
        try {
            val putObjectRequest = createPutObjectRequest(bucketName!!, filePath, file)
            val requestBody = RequestBody.fromInputStream(file.inputStream, file.size)
            s3Client.putObject(putObjectRequest, requestBody)

            return filePath
        } catch (e: S3Exception) {
            logger.error("Failed to store file", e)
            throw FailedToStoreFileException()
        }
    }

    private fun createPutObjectRequest(bucketName: String, fileName: String, file: MultipartFile): PutObjectRequest {
        return PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType(file.contentType)
            .contentLength(file.size)
            .build()
    }

    private fun createGetObjectRequest(bucketName: String, fileName: String): GetObjectRequest {
        return GetObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .build()
    }

    private fun generatePreSignedUrlRequest(bucketName: String, s3FilePath: String): GetObjectPresignRequest {
        return GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .getObjectRequest(createGetObjectRequest(bucketName, s3FilePath))
            .build()
    }
}