package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.exceptions.ResumeNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class UserServiceUtilsImpl(
    webClientBuilder: WebClient.Builder
) : UserServiceUtils {

    private val webClient: WebClient = webClientBuilder.build()
    private val companyNameCache = ConcurrentHashMap<UUID, String>()
    private val s3ResumePathCache = ConcurrentHashMap<UUID, String>()

    @Value("\${user.service.url}")
    lateinit var userServiceUrl: String

    private fun createHeaders(): Map<String, String> {
        val authentication = SecurityContextHolder.getContext().authentication
        return mapOf(
            "X-User-Id" to authentication.name,
            "X-User-Roles" to authentication.authorities.joinToString(",") { it.authority }
        )
    }

    private fun executeGraphQLQuery(query: String): Map<String, Any?>? {
        val headers = createHeaders()
        return webClient.post()
            .uri("$userServiceUrl/graphql")
            .headers { it.setAll(headers) }
            .bodyValue(mapOf("query" to query))
            .retrieve()
            .bodyToMono(Any::class.java)
            .block()?.let {
                @Suppress("UNCHECKED_CAST")
                it as? Map<String, Any?>
            }
    }

    private fun executeRestRequest(uri: String): Map<String, Any?>? {
        return webClient.get()
            .uri("$userServiceUrl/$uri")
            .retrieve()
            .bodyToMono(Any::class.java)
            .block()?.let {
                @Suppress("UNCHECKED_CAST")
                it as? Map<String, Any?>
            }
    }

    override fun getRecruiterCompany(): UUID {
        val query = """
            query {
                recruiterCompany
            }
        """.trimIndent()

        val response = executeGraphQLQuery(query)
        val companyId = (response?.get("data") as? Map<*, *>)?.get("recruiterCompany") as? String
            ?: throw IllegalStateException("Recruiter company ID not found")

        return UUID.fromString(companyId)
    }

    override fun getApplicantFullName(userId: UUID): String {
        val query = """
            query {
                userFullName(userId: "$userId")
            }
        """.trimIndent()

        val response = executeGraphQLQuery(query)
        return (response?.get("data") as? Map<*, *>)?.get("userFullName") as? String
            ?: throw IllegalStateException("User full name not found")
    }

    override fun getCompanyName(companyId: UUID): String {
        return companyNameCache.computeIfAbsent(companyId) {
            val uri = "getCompanyById?companyId=$companyId"

            val response = executeRestRequest(uri)
            (response as? Map<*, *>)?.get("name") as? String
                ?: throw IllegalStateException("Company name not found")
        }
    }

    override fun getS3ResumePath(resumeId: UUID): String {
        return s3ResumePathCache.computeIfAbsent(resumeId) {
            val query = """
                query {
                    resumeById(resumeId: "$resumeId") {
                        s3ResumePath
                    }
                }
            """.trimIndent()


            val response = executeGraphQLQuery(query)
            (response?.get("data") as? Map<*, *>)?.get("resumeById")?.let { it as? Map<*, *> }?.get("s3ResumePath") as? String
                ?: throw ResumeNotFoundException("Resume not found by id: $resumeId")
        }
    }
}
