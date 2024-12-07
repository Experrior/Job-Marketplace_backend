package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.FollowedJobs
import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.entities.enums.EmploymentType
import com.jobsearch.jobservice.entities.enums.ExperienceLevel
import com.jobsearch.jobservice.entities.enums.WorkLocation
import com.jobsearch.jobservice.entities.specifications.JobSpecifications
import com.jobsearch.jobservice.exceptions.JobNotFoundException
import com.jobsearch.jobservice.replica_repositories.JobRepositoryReplica
import com.jobsearch.jobservice.repositories.FollowedJobRepository
import com.jobsearch.jobservice.repositories.JobRepository
import com.jobsearch.jobservice.requests.JobFilterRequest
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.responses.DeleteJobResponse
import com.jobsearch.jobservice.responses.FollowJobResponse
import com.jobsearch.jobservice.responses.JobResponse
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class JobServiceImpl(
    private val jobRepository: JobRepository,
    private val jobRepositoryReplica: JobRepositoryReplica,
    private val followedJobRepository: FollowedJobRepository,
    private val quizService: QuizService,
    private val userServiceUtils: UserServiceUtils,
    private val viewedJobService: ViewedJobService
): JobService {
    private val logger = LoggerFactory.getLogger(JobServiceImpl::class.java)

    override fun createJob(jobRequest: JobRequest): JobResponse {
        val companyId = userServiceUtils.getRecruiterCompany()

        val job = mapRequestToJob(jobRequest, companyId, getRecruiterId())
        val savedJob = jobRepository.save(job)
        return mapJobToResponse(savedJob)
    }

    override fun deleteJobById(jobId: UUID): DeleteJobResponse {
        return try {
            val job = getJobEntityById(jobId)

            if (job.isDeleted) {
                return DeleteJobResponse(success = false, message = "Job is already deleted")
            }

            job.isDeleted = true
            jobRepository.save(job)
            DeleteJobResponse(success = true, message = "Job deleted")
        } catch (e: JobNotFoundException) {
            DeleteJobResponse(success = false, message = "Job not found")
        } catch (e: Exception) {
            DeleteJobResponse(success = false, message = e.message ?: "An unexpected error occurred")
        }
    }

    override fun updateJobById(jobId: UUID, jobRequest: JobRequest): JobResponse {
        val existingJob = jobRepository.findById(jobId)
        if (existingJob.isEmpty) {
            throw JobNotFoundException(jobId)
        }
        val updatedJob = jobRepository.save(mapRequestToJob(jobRequest, userServiceUtils.getRecruiterCompany(), getRecruiterId(), jobId = jobId))
        return mapJobToResponse(updatedJob)    }

    override fun getJobsByRecruiter(recruiterId: UUID): List<JobResponse> {
        return jobRepositoryReplica.findJobsByRecruiterId(recruiterId).map { mapJobToResponse(it) }
    }

    override fun getJobEntityById(jobId: UUID): Job {
        return jobRepositoryReplica.findJobByJobId(jobId) ?: throw JobNotFoundException(jobId)
    }

    override fun getJobById(jobId: UUID): JobResponse {
        return mapJobToResponse(getJobEntityById(jobId))
    }

    override fun getJobByIdAndDeleteFalse(userId: UUID?, jobId: UUID): JobResponse {
        val job = jobRepository.findJobByJobIdAndIsDeletedFalse(jobId) ?: throw JobNotFoundException(jobId)
        if(userId != null){
            viewedJobService.viewJob(userId, jobId)
        }
        return mapJobToResponse(job)
    }

    override fun getFilteredJobs(filter: JobFilterRequest?, pageable: Pageable): Page<JobResponse> {
        val specification: Specification<Job>? = filter?.let { JobSpecifications.getJobsByFilter(it) }
        return jobRepositoryReplica.findAll(specification, pageable).map { mapJobToResponse(it) }
    }

    override fun restoreJobById(jobId: UUID): JobResponse {
        val job = getJobEntityById(jobId)
        job.isDeleted = false

        val restoredJob = jobRepository.save(job)
        return mapJobToResponse(restoredJob)
    }

    @Transactional
    override fun toggleFollowJob(jobId: UUID, userId: UUID): FollowJobResponse {
        val job = getJobEntityById(jobId)

        val isFollowed = followedJobRepository.existsByUserIdAndJobId(userId, jobId)

        if (isFollowed) {
            followedJobRepository.deleteByUserIdAndJobId(userId, jobId)
            return FollowJobResponse(
                success = true,
                message = "Job successfully unfollowed.",
                isFollowed = false
            )
        } else {
            followedJobRepository.save(FollowedJobs(userId = userId, jobId = jobId, job = job))
            return FollowJobResponse(
                success = true,
                message = "Job successfully followed.",
                isFollowed = true
            )
        }
    }

    override fun getFollowedFilteredJobs(userId: UUID): List<JobResponse> {
        return followedJobRepository.findByUserId(userId).map { mapJobToResponse(it.job!!) }
    }

    override fun getJobsByCompany(companyId: UUID): List<JobResponse> {
        return jobRepositoryReplica.findJobsByCompanyIdAndIsDeletedFalse(companyId).map { mapJobToResponse(it) }
    }

    private fun mapRequestToJob(jobRequest: JobRequest, companyId: UUID, recruiterId: UUID, jobId: UUID? = null): Job {
        logger.info("Job request: $jobRequest")
        return Job(
            jobId = jobId,
            recruiterId = recruiterId,
            companyId = companyId,
            title = jobRequest.title,
            description = jobRequest.description,
            location = jobRequest.location,
            employmentType = jobRequest.employmentType?.let { EmploymentType.valueOf(it.uppercase()) },
            workLocation = jobRequest.workLocation?.let { WorkLocation.valueOf(it.uppercase()) },
            salary = jobRequest.salary,
            requiredSkills = jobRequest.requiredSkills,
            requiredExperience = jobRequest.requiredExperience,
            experienceLevel = jobRequest.experienceLevel?.let { ExperienceLevel.valueOf(it.uppercase()) },
            quiz = jobRequest.quizId?.let { quizService.findQuizEntityById(it) },
        )
    }

    override fun mapJobToResponse(job: Job): JobResponse {
        return JobResponse(
            jobId = job.jobId ?: throw IllegalArgumentException("Job ID cannot be null"),
            recruiterId = job.recruiterId,
            companyId = job.companyId,
            title = job.title,
            description = job.description,
            location = job.location,
            employmentType = job.employmentType?.value,
            workLocation = job.workLocation?.value,
            salary = job.salary,
            requiredSkills = job.requiredSkills,
            requiredExperience = job.requiredExperience,
            experienceLevel = job.experienceLevel?.value,
            createdAt = job.createdAt,
            updatedAt = job.updatedAt,
            isDeleted = job.isDeleted,
            quizId = job.quiz?.quizId,
            companyName = userServiceUtils.getCompanyName(job.companyId),
        )
    }

    private fun getRecruiterId(): UUID{
        val authentication = SecurityContextHolder.getContext().authentication
        return UUID.fromString(authentication.name)
    }
}