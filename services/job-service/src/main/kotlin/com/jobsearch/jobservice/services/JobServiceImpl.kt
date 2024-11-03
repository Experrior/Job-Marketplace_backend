package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.exceptions.JobNotFoundException
import com.jobsearch.jobservice.repositories.JobRepository
import com.jobsearch.jobservice.requests.JobRequest
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

    override fun deleteJobById(jobId: UUID) {
        jobRepository.deleteById(jobId)
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

    override fun getJobs(): List<Job> {
        return jobRepository.findAll()
    }

    override fun getJobById(jobId: UUID): Job {
        return jobRepository.findJobByJobId(jobId)
            ?: throw JobNotFoundException(jobId)
    }

    override fun getJobsByCompany(companyId: UUID): List<Job> {
        return jobRepository.findJobsByCompanyId(companyId)
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