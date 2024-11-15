package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.entities.specifications.JobSpecifications
import com.jobsearch.jobservice.exceptions.JobNotFoundException
import com.jobsearch.jobservice.repositories.JobRepository
import com.jobsearch.jobservice.requests.JobFilterRequest
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.responses.DeleteJobResponse
import com.jobsearch.jobservice.responses.JobResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class JobServiceImpl(
    private val jobRepository: JobRepository,
    webClientBuilder: WebClient.Builder,
    private val quizService: QuizService
): JobService {
    private val webClient: WebClient = webClientBuilder.build()

    @Value("\${user.service.url}")
    val userServiceUrl: String? = null

    override fun createJob(jobRequest: JobRequest): JobResponse {
        val companyId = getRecruiterCompany()

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
        val updatedJob = jobRepository.save(mapRequestToJob(jobRequest, getRecruiterCompany(), getRecruiterId(), jobId = jobId))
        return mapJobToResponse(updatedJob)    }

    override fun getJobsByRecruiter(recruiterId: UUID): List<JobResponse> {
        return jobRepository.findJobsByRecruiterId(recruiterId).map { mapJobToResponse(it) }
    }

    override fun getJobEntityById(jobId: UUID): Job {
        return jobRepository.findJobByJobId(jobId) ?: throw JobNotFoundException(jobId);
    }

    override fun getJobById(jobId: UUID): JobResponse {
        return mapJobToResponse(getJobEntityById(jobId))
    }

    override fun getJobByIdAndDeleteFalse(jobId: UUID): JobResponse {
        val job = jobRepository.findJobByJobIdAndIsDeletedFalse(jobId) ?: throw JobNotFoundException(jobId)
        return mapJobToResponse(job)
    }

    override fun getFilteredJobs(filter: JobFilterRequest?, pageable: Pageable): Page<JobResponse> {
        val specification: Specification<Job>? = filter?.let { JobSpecifications.getJobsByFilter(it) }
        return jobRepository.findAll(specification, pageable).map { mapJobToResponse(it) }
    }

    override fun restoreJobById(jobId: UUID): JobResponse {
        val job = getJobEntityById(jobId)
        val restoredJob = jobRepository.save(job)
        return mapJobToResponse(restoredJob)
    }

    override fun getJobsByCompany(companyId: UUID): List<JobResponse> {
        return jobRepository.findJobsByCompanyIdAndIsDeletedFalse(companyId).map { mapJobToResponse(it) }
    }

    private fun mapRequestToJob(jobRequest: JobRequest, companyId: UUID, recruiterId: UUID, jobId: UUID? = null): Job {
        return Job(
            jobId = jobId,
            recruiterId = recruiterId,
            companyId = companyId,
            title = jobRequest.title,
            description = jobRequest.description,
            location = jobRequest.location,
            salary = jobRequest.salary,
            requiredSkills = jobRequest.requiredSkills,
            requiredExperience = jobRequest.requiredExperience,
            quiz = jobRequest.quizId?.let { quizService.findQuizEntityById(it) },
        )
    }

    private fun mapJobToResponse(job: Job): JobResponse {
        return JobResponse(
            jobId = job.jobId ?: throw IllegalArgumentException("Job ID cannot be null"),
            recruiterId = job.recruiterId,
            companyId = job.companyId,
            title = job.title,
            description = job.description,
            location = job.location,
            salary = job.salary,
            requiredSkills = job.requiredSkills,
            requiredExperience = job.requiredExperience,
            createdAt = job.createdAt,
            updatedAt = job.updatedAt,
            isDeleted = job.isDeleted,
            quizId = job.quiz?.quizId
        )
    }

    private fun getRecruiterCompany(): UUID {
        val query = """
            query {
                recruiterCompany
            }
        """.trimIndent()

        val authentication = SecurityContextHolder.getContext().authentication
        val userId = authentication.name
        val roles = authentication.authorities.joinToString(",") { it.authority }
        val response = webClient.post()
            .uri("$userServiceUrl/graphql")
            .header("X-User-Id", userId)
            .header("X-User-Roles", roles)
            .bodyValue(mapOf("query" to query))
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        val companyId = (response?.get("data") as Map<*, *>)["recruiterCompany"] as String
        return UUID.fromString(companyId)
    }

    private fun getRecruiterId(): UUID{
        val authentication = SecurityContextHolder.getContext().authentication
        return UUID.fromString(authentication.name)
    }
}