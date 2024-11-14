package com.jobsearch.jobservice.exceptions

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import jakarta.validation.ConstraintViolationException
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class GlobalExceptionHandler {
    @GraphQlExceptionHandler(JobNotFoundException::class)
    fun handleJobNotFoundExceptionGraphQL(
        ex: JobNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message("Job not found: ${ex.jobId}")
            .build()
    }

    @GraphQlExceptionHandler(ApplicationNotFoundException::class)
    fun handleApplicationNotFoundException(
        ex: ApplicationNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message("Application not found: ${ex.applicationId}")
            .build()
    }

    @GraphQlExceptionHandler(QuizNotFoundException::class)
    fun handleQuizNotFoundException(
        ex: QuizNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message("Quiz not found by jobId: ${ex.jobId}")
            .build()
    }

    @GraphQlExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccessException(
        ex: IllegalAccessException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @ExceptionHandler(JobNotFoundException::class)
    fun handleJobNotFoundExceptionRest(
        ex: JobNotFoundException
    ): ResponseEntity<String> {
        return ResponseEntity("Job not found: ${ex.jobId}", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FailedToStoreFileException::class)
    fun handleFailedToStoreFileException(
        ex: FailedToStoreFileException
    ): ResponseEntity<String> {
        return ResponseEntity("Failed to store file", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(FileAlreadyExistsException::class)
    fun handleFileAlreadyExistsException(
        ex: FileAlreadyExistsException
    ): ResponseEntity<String> {
        return ResponseEntity("File already exists with name: ${ex.fileName}", HttpStatus.CONFLICT)
    }

    @ExceptionHandler(EmptyFileException::class)
    fun handleEmptyFileException(
        ex: EmptyFileException
    ): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserAlreadyAppliedException::class)
    fun handleUserAlreadyAppliedException(
        ex: UserAlreadyAppliedException
    ): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: WebRequest
    ): ResponseEntity<String> {
        ex.printStackTrace()
        return ResponseEntity("Required request body is missing or unreadable", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(FileSizeExceededException::class)
    fun handleFileSizeExceededException(
        ex: FileSizeExceededException
    ): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.PAYLOAD_TOO_LARGE)
    }

    @ExceptionHandler(InvalidFileTypeException::class)
    fun handleInvalidFileTypeException(
        ex: InvalidFileTypeException
    ): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }

    @GraphQlExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptionForGraphql(ex: ConstraintViolationException): GraphQLError {
        val errors: MutableMap<String, String?> = HashMap()

        ex.constraintViolations.forEach { violation ->
            val fieldName = violation.propertyPath.toString().substringAfterLast('.')
            val errorMessage = violation.message
            errors[fieldName] = errorMessage
        }

        return GraphqlErrorBuilder.newError()
            .message("Validation error")
            .extensions(mapOf("errors" to errors))
            .build()
    }
}