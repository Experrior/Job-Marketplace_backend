package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Application
import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.entities.enums.ApplicationStatus
import com.jobsearch.jobservice.exceptions.ApplicationNotFoundException
import com.jobsearch.jobservice.exceptions.UserAlreadyAppliedException
import com.jobsearch.jobservice.repositories.ApplicationRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class JobApplicationServiceImpl(
    private val applicationRepository: ApplicationRepository,
    private val jobService: JobService
): JobApplicationService {
    override fun applyForJob(jobId: UUID, userId: UUID): Application {
        val job = getJob(jobId)
        checkUserAlreadyApplied(job, userId)
        val application = createApplication(job, userId)
        return applicationRepository.save(application)
    }

    override fun getUserApplications(userId: UUID): List<Application> {
        return applicationRepository.findApplicationsByUserId(userId)
    }

    override fun getJobApplications(jobId: UUID): List<Application> {
        val job = getJob(jobId)
        return applicationRepository.findApplicationsByJob(job)
    }

    override fun setApplicationStatus(applicationId: UUID, status: ApplicationStatus) {
        val application = getApplication(applicationId)
        application.status = status
        applicationRepository.save(application)
    }

    private fun createApplication(job: Job, userId: UUID): Application {
        return Application(
            userId = userId,
            job = job,
            status = ApplicationStatus.PENDING
        )
    }

    private fun getJob(jobId: UUID): Job {
        return jobService.getJobById(jobId)
    }

    private fun getApplication(applicationId: UUID): Application {
        return applicationRepository.findApplicationByApplicationId(applicationId)
            ?: throw ApplicationNotFoundException(applicationId)
    }

    private fun checkUserAlreadyApplied(job: Job, userId: UUID) {
        if(applicationRepository.findApplicationByJobAndUserId(job, userId) != null)
            throw job.jobId?.let { UserAlreadyAppliedException(userId, it) }!!
    }
}