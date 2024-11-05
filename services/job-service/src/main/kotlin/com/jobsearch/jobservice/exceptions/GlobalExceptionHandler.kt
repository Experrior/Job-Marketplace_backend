package com.jobsearch.jobservice.exceptions

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(JobNotFoundException::class)
    fun handleJobNotFoundException(
        ex: JobNotFoundException
    ): ResponseEntity<String> {
        return ResponseEntity(
            "Job not found ${ex.jobId}",
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(ApplicationNotFoundException::class)
    fun handleApplicationNotFoundException(
        ex: ApplicationNotFoundException
    ): ResponseEntity<String> {
        return ResponseEntity(
            "Application not found ${ex.applicationId}",
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(UserAlreadyAppliedException::class)
    fun handleUserAlreadyAppliedException(
        ex: UserAlreadyAppliedException
    ): ResponseEntity<String> {
        return ResponseEntity(
            ex.message,
            HttpStatus.FORBIDDEN
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: WebRequest
    ): ResponseEntity<String> {
        ex.printStackTrace()
        return ResponseEntity("Required request body is missing or unreadable", HttpStatus.BAD_REQUEST)
    }

    @GraphQlExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }
}