package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import com.jobsearch.jobservice.exceptions.ApplicationNotFoundException
import com.jobsearch.jobservice.exceptions.UserAlreadyAppliedException
import com.jobsearch.jobservice.repositories.ApplicationRepository
import com.jobsearch.jobservice.responses.SetApplicationStatusResponse
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class JobApplicationServiceImpl(
    private val applicationRepository: ApplicationRepository,
    private val jobService: JobService,
    private val resumeStorageService: ResumeStorageService
): JobApplicationService {
    override fun applyForJob(jobId: UUID, userId: UUID, resume: MultipartFile): Application {
        val job = getJob(jobId)
        checkUserAlreadyApplied(job, userId)
        val s3ResumePath = resumeStorageService.storeResume(userId, jobId, resume)
        val application = createApplication(job, userId, s3ResumePath)
        return applicationRepository.save(application)
    }

    override fun getUserApplications(userId: UUID): List<Application> {
        val applications = applicationRepository.findApplicationsByUserId(userId)
        setResumeUrls(applications)
        return applications
    }

    override fun getJobApplications(jobId: UUID): List<Application> {
        val job = getJob(jobId)
        val applications = applicationRepository.findApplicationsByJob(job)
        setResumeUrls(applications)
        return applications
    }

    override fun setApplicationStatus(applicationId: UUID, status: ApplicationStatus): SetApplicationStatusResponse {
        val application = getApplication(applicationId)
        application.status = status
        applicationRepository.save(application)
        return SetApplicationStatusResponse(success = true, message = "Application status updated")
    }

    private fun createApplication(job: Job, userId: UUID, s3ResumePath: String): Application {
        return Application(
            userId = userId,
            job = job,
            status = ApplicationStatus.PENDING,
            s3ResumePath = s3ResumePath
        )
    }

    private fun getJob(jobId: UUID): Job {
        return jobService.getJobById(jobId)
    }

    private fun getApplication(applicationId: UUID): Application {
        val application = applicationRepository.findApplicationByApplicationId(applicationId)
            ?: throw ApplicationNotFoundException(applicationId)

        setResumeUrls(listOf(application))
        return application
    }

    private fun checkUserAlreadyApplied(job: Job, userId: UUID) {
        if(applicationRepository.findApplicationByJobAndUserId(job, userId) != null)
            throw job.jobId?.let { UserAlreadyAppliedException(userId, it) }!!
    }

    private fun setResumeUrls(applications: List<Application>) {
        applications.forEach { application ->
            application.resumeUrl = application.s3ResumePath?.let { resumeStorageService.getResumeUrl(it) }
        }
    }

}