package com.jobsearch.apigateway.config

import org.springframework.util.AntPathMatcher

enum class PublicEndpoint(val pattern: String) {
    PUBLIC("/public"),
    LOGIN("/user-service/login"),
    REGISTER("/user-service/register/**"),
    VERIFICATION("/user-service/verification/**"),
    TOKEN("/user-service/token/**"),
    RESET_PASSWORD("/user-service/password/reset"),
    UPDATE_PASSWORD("/user-service/password/update"),
    VALIDATE_TOKEN("/user-service/password/validateToken"),
    GET_COMPANIES("/user-service/getCompanies"),
    GET_JOBS("/job-service/getJobs"),
    GET_COMPANY_BY_ID("/user-service/getCompanyById"),
    ERROR("/user-service/error");



    companion object {
        private val pathMatcher = AntPathMatcher()

        fun isPublicPath(path: String): Boolean {
            return entries.any { endpoint ->
                pathMatcher.match(endpoint.pattern, path)
            }
        }
    }
}
