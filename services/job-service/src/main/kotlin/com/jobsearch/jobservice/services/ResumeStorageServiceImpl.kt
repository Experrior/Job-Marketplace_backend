package com.jobsearch.jobservice.services

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
class ResumeStorageServiceImpl(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner
): ResumeStorageService {
    private val logger = LoggerFactory.getLogger(ResumeStorageServiceImpl::class.java)

    @Value("\${aws.s3.bucket}")
    val bucketName: String? = null

    override fun storeResume(userId: UUID, jobId: UUID, resume: MultipartFile): String {
        val fileName = "resumes/${jobId}_${userId}_${resume.originalFilename}"

        try {
            val putObjectRequest = createPutObjectRequest(bucketName!!, fileName, resume)

            val requestBody = RequestBody.fromInputStream(resume.inputStream, resume.size)
            s3Client.putObject(putObjectRequest, requestBody)

            return fileName
        }catch (e: S3Exception) {
            logger.error("Failed to store resume", e)
            throw RuntimeException("Failed to store resume")
        }
    }

    override fun getResumeUrl(s3FilePath: String): String {
        val presignRequest = generatePreSignedUrlRequest(bucketName!!, s3FilePath)
        val presignedURL = s3Presigner.presignGetObject(presignRequest)

        return presignedURL.url().toString()
    }

    private fun createPutObjectRequest(bucketName: String, fileName: String, resume: MultipartFile): PutObjectRequest {
        return PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType("application/pdf")
            .contentLength(resume.size)
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