package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.exceptions.JobNotFoundException
import com.jobsearch.jobservice.repositories.JobRepository
import com.jobsearch.jobservice.requests.JobRequest
import com.jobsearch.jobservice.responses.DeleteJobResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class JobServiceImpl(
    private val jobRepository: JobRepository,
    webClientBuilder: WebClient.Builder
): JobService {
    private val webClient: WebClient = webClientBuilder.build()

    override fun createJob(jobRequest: JobRequest): Job {
        val companyId = getRecruiterCompany()

        println("Company ID: $companyId")
        val job = mapRequestToJob(jobRequest, companyId, getRecruiterId())
        return jobRepository.save(job)
    }

    override fun deleteJobById(jobId: UUID): DeleteJobResponse {
        return try {
            val job = getJobById(jobId)

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

    override fun updateJobById(jobId: UUID, jobRequest: JobRequest): Job {
        val existingJob = jobRepository.findById(jobId)
        if (existingJob.isEmpty) {
            throw JobNotFoundException(jobId)
        }
        return jobRepository.save(mapRequestToJob(jobRequest, getRecruiterCompany(), getRecruiterId(), jobId = jobId))
    }

    override fun getJobsByRecruiter(recruiterId: UUID): List<Job> {
        return jobRepository.findJobsByRecruiterId(recruiterId)
    }

    override fun getJobs(limit: Int?, offset: Int?): Page<Job> {
        val pageLimit = limit ?: 20
        val pageOffset = offset ?: 0

        val pageable: Pageable = PageRequest.of(pageOffset / pageLimit, pageLimit)
        return jobRepository.findAllByIsDeletedFalse(pageable)
    }

    override fun getJobById(jobId: UUID): Job {
        return jobRepository.findJobByJobId(jobId)
            ?: throw JobNotFoundException(jobId)
    }

    override fun getJobByIdAndDeleteFalse(jobId: UUID): Job {
        return jobRepository.findJobByJobIdAndIsDeletedFalse(jobId)
            ?: throw JobNotFoundException(jobId)
    }

    override fun restoreJobById(jobId: UUID): Job {
        val job = getJobById(jobId)
        job.isDeleted = false
        return jobRepository.save(job)
    }

    override fun getJobsByCompany(companyId: UUID): List<Job> {
        return jobRepository.findJobsByCompanyIdAndIsDeletedFalse(companyId)
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
            requiredExperience = jobRequest.requiredExperience
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
        println("Roles: $roles")
        val response = webClient.post()
            .uri("http://user-service/graphql")
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