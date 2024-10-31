package com.jobsearch.jobservice.exceptions

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
class GlobalExceptionHandler {
    @GraphQlExceptionHandler(JobNotFoundException::class)
    fun handleJobNotFoundException(
        ex: JobNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }
}