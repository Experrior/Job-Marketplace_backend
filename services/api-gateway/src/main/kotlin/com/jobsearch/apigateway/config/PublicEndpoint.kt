package com.jobsearch.apigateway.config

import org.springframework.util.AntPathMatcher

enum class PublicEndpoint(val pattern: String) {
    PUBLIC("/public"),
    LOGIN("/user-service/login"),
    REGISTER("/user-service/register/**"),
    VERIFICATION("/user-service/verification/**"),
    TOKEN("/user-service/token/**"),
    RESET_PASSWORD("/user-service/resetPassword"),
    UPDATE_PASSWORD("/user-service/updatePassword"),
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
