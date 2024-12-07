package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import com.jobsearch.jobservice.exceptions.ApplicationNotFoundException
import com.jobsearch.jobservice.exceptions.QuizResultNotFoundException
import com.jobsearch.jobservice.exceptions.UserAlreadyAppliedException
import com.jobsearch.jobservice.repositories.ApplicationRepository
import com.jobsearch.jobservice.replica_repositories.ApplicationRepositoryReplica
import com.jobsearch.jobservice.responses.ApplicationResponse
import com.jobsearch.jobservice.responses.SetApplicationStatusResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class JobApplicationServiceImpl(
    private val applicationRepository: ApplicationRepository,
    private val applicationRepositoryReplica: ApplicationRepositoryReplica,
    private val jobService: JobService,
    private val fileStorageService: FileStorageService,
    private val quizResultService: QuizResultService,
    private val userServiceUtils: UserServiceUtils
): JobApplicationService {
    private val logger = LoggerFactory.getLogger(JobApplicationService::class.java)

    override fun applyForJob(jobId: UUID, userId: UUID, resumeId: UUID, quizResultId: UUID?): Application {
        val job = getJob(jobId)

        checkUserAlreadyApplied(job, userId)
        checkQuizRequirement(job, quizResultId)
        checkQuizResultBelongsToUser(quizResultId, userId)

        val application = createApplication(job, userId, resumeId, quizResultId)
        setResumeUrls(listOf(application))
        setFullName(listOf(application))
        return applicationRepository.save(application)
    }

    override fun getUserApplications(userId: UUID): List<ApplicationResponse> {
        logger.info("Getting applications for user: $userId")
        val applications = applicationRepositoryReplica.findApplicationsByUserId(userId)
        setResumeUrls(applications)
        setFullName(applications)
        return applications.map { mapApplicationToResponse(it) }
    }

    override fun getJobApplications(jobId: UUID): List<Application> {
        val job = getJob(jobId)
        val applications = applicationRepositoryReplica.findApplicationsByJob(job)
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

    private fun createApplication(job: Job, userId: UUID, resumeId: UUID, quizResultId: UUID?): Application {
        val quizResult = quizResultId?.let { quizResultService.getQuizResultEntityById(it) }
        if (job.quiz != null && quizResult == null)
            throw QuizResultNotFoundException("Quiz result not found")

        return Application(
            userId = userId,
            job = job,
            status = ApplicationStatus.PENDING,
            s3ResumePath = userServiceUtils.getS3ResumePath(resumeId),
            quizResult = quizResult
        )
    }

    private fun getJob(jobId: UUID): Job {
        return jobService.getJobEntityById(jobId)
    }

    private fun getApplication(applicationId: UUID): Application {
        val application = applicationRepositoryReplica.findApplicationByApplicationId(applicationId)
            ?: throw ApplicationNotFoundException(applicationId)

        setResumeUrls(listOf(application))
        setFullName(listOf(application))
        return application
    }

    private fun checkUserAlreadyApplied(job: Job, userId: UUID) {
        if(applicationRepositoryReplica.findApplicationByJobAndUserId(job, userId) != null)
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

    private fun checkQuizResultBelongsToUser(quizResultId: UUID?, userId: UUID) {
        if (quizResultId != null && quizResultService.getQuizResultEntityById(quizResultId).applicantId != userId)
            throw QuizResultNotFoundException("Quiz result does not belong to the user")
    }

    private fun checkQuizRequirement(job: Job, quizResultId: UUID?) {
        if (job.quiz != null && quizResultId == null) {
            throw QuizResultNotFoundException("Quiz result is required for this job")
        }
    }

    private fun mapApplicationToResponse(application: Application): ApplicationResponse {
        logger.info("Mapping application to response: ${application.applicationId}")
        return ApplicationResponse(
            applicationId = application.applicationId!!,
            userId = application.userId,
            job = jobService.mapJobToResponse(application.job),
            resumeUrl = application.resumeUrl,
            status = application.status,
            quizResult = application.quizResult?.let { quizResultService.mapQuizResultToResponse(it) },
            createdAt = application.createdAt,
            updatedAt = application.updatedAt
        )
    }

}