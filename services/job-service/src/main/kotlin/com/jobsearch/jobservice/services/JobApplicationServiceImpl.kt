package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import com.jobsearch.jobservice.exceptions.*
import com.jobsearch.jobservice.repositories.ApplicationRepository
import com.jobsearch.jobservice.responses.ApplyForJobResponse
import com.jobsearch.jobservice.responses.SetApplicationStatusResponse
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class JobApplicationServiceImpl(
    private val applicationRepository: ApplicationRepository,
    private val jobService: JobService,
    private val fileStorageService: FileStorageService,
    private val quizResultService: QuizResultService,
    private val userServiceUtils: UserServiceUtils
): JobApplicationService {
    override fun applyForJob(jobId: UUID, userId: UUID, resume: MultipartFile, quizResultId: UUID?): ApplyForJobResponse {
        checkResumeIsEmpty(resume)
        checkResumeSize(resume)
        checkResumeType(resume)

        val job = getJob(jobId)
        checkUserAlreadyApplied(job, userId)
        val s3ResumePath = fileStorageService.storeResume(userId, jobId, resume)
        val application = createApplication(job, userId, s3ResumePath, quizResultId)
        return convertToResponse(applicationRepository.save(application))
    }

    override fun getUserApplications(userId: UUID): List<Application> {
        val applications = applicationRepository.findApplicationsByUserId(userId)
        setResumeUrls(applications)
        setFullName(applications)
        return applications
    }

    override fun getJobApplications(jobId: UUID): List<Application> {
        val job = getJob(jobId)
        val applications = applicationRepository.findApplicationsByJob(job)
        setResumeUrls(applications)
        setFullName(applications)
        return applications
    }

    override fun setApplicationStatus(applicationId: UUID, status: ApplicationStatus): SetApplicationStatusResponse {
        val application = getApplication(applicationId)
        application.status = status
        applicationRepository.save(application)
        return SetApplicationStatusResponse(success = true, message = "Application status updated")
    }

    private fun createApplication(job: Job, userId: UUID, s3ResumePath: String, quizResultId: UUID?): Application {
        val quizResult = quizResultId?.let { quizResultService.getQuizResultEntityById(it) }
        if (job.quiz != null && quizResult == null)
            throw QuizResultNotFoundException("Quiz result not found")

        return Application(
            userId = userId,
            job = job,
            status = ApplicationStatus.PENDING,
            s3ResumePath = s3ResumePath,
            quizResult = quizResult
        )
    }

    private fun getJob(jobId: UUID): Job {
        return jobService.getJobEntityById(jobId)
    }

    private fun getApplication(applicationId: UUID): Application {
        val application = applicationRepository.findApplicationByApplicationId(applicationId)
            ?: throw ApplicationNotFoundException(applicationId)

        setResumeUrls(listOf(application))
        setFullName(listOf(application))
        return application
    }

    private fun checkUserAlreadyApplied(job: Job, userId: UUID) {
        if(applicationRepository.findApplicationByJobAndUserId(job, userId) != null)
            throw job.jobId?.let { UserAlreadyAppliedException(userId, it) }!!
    }

    private fun setResumeUrls(applications: List<Application>) {
        applications.forEach { application ->
            application.resumeUrl = application.s3ResumePath?.let { fileStorageService.getFileUrl(it) }
        }
    }

    private fun setFullName(applications: List<Application>) {
        applications.forEach { application ->
            application.fullName = application.userId.let { userServiceUtils.getApplicantFullName(it) }
        }
    }

    private fun convertToResponse(application: Application): ApplyForJobResponse {
        return ApplyForJobResponse(
            applicationId = application.applicationId!!,
            userId = application.userId,
            job = application.job,
            createdAt = application.createdAt,
            status = application.status
        )
    }

    private fun checkResumeIsEmpty(resume: MultipartFile) {
        if(resume.isEmpty)
            throw EmptyFileException()
    }

    private fun checkResumeType(resume: MultipartFile) {
        if (resume.contentType != "application/pdf") {
            throw InvalidFileTypeException()
        }
    }

    private fun checkResumeSize(resume: MultipartFile) {
        if (resume.size > 2 * 1024 * 1024) {
            throw FileSizeExceededException()
        }
    }

}