package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.Job
import com.jobsearch.jobservice.repositories.JobRepository
import com.jobsearch.jobservice.requests.JobRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Service
class JobServiceImpl(
    private val jobRepository: JobRepository,
    private val webClient: WebClient
): JobService {

    override fun createJob(jobRequest: JobRequest): Job {
        val companyId = getRecruiterCompany()

        println("Company ID: $companyId")
        val job = mapRequestToJob(jobRequest, companyId)
        return jobRepository.save(job)
    }

    private fun mapRequestToJob(jobRequest: JobRequest, companyId: UUID): Job {
        return Job(
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

        val response = webClient.post()
            .uri("/user-service/graphql")
            .header("X-User-Id", userId)
            .header("X-User-Roles", roles)
            .bodyValue(mapOf("query" to query))
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        val companyId = (response?.get("data") as Map<*, *>)["recruiterCompany"] as String
        return UUID.fromString(companyId)
    }
}