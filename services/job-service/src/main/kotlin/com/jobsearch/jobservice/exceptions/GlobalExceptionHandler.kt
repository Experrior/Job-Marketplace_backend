package com.jobsearch.jobservice.exceptions

import com.jobsearch.jobservice.responses.ApiResponse
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
    @ExceptionHandler(QuizResultNotFoundException::class)
    fun handleQuizResultNotFoundException(
        ex: QuizResultNotFoundException
    ): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.NOT_FOUND.name,
            message = ex.message ?: "Quiz result not found",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(apiResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(JobNotFoundException::class)
    fun handleJobNotFoundExceptionRest(
        ex: JobNotFoundException
    ): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.NOT_FOUND.name,
            message = "Job not found: ${ex.jobId}",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(apiResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FailedToStoreFileException::class)
    fun handleFailedToStoreFileException(
        ex: FailedToStoreFileException
    ): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.name,
            message = "Failed to store file",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(FileAlreadyExistsException::class)
    fun handleFileAlreadyExistsException(
        ex: FileAlreadyExistsException
    ): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.CONFLICT.name,
            message = "File already exists with name: ${ex.fileName}",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(apiResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(EmptyFileException::class)
    fun handleEmptyFileException(
        ex: EmptyFileException
    ): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.BAD_REQUEST.name,
            message = ex.message ?: "Empty file",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(apiResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserAlreadyAppliedException::class)
    fun handleUserAlreadyAppliedException(
        ex: UserAlreadyAppliedException
    ): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.CONFLICT.name,
            message = ex.message ?: "User already applied",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(apiResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: WebRequest
    ): ResponseEntity<ApiResponse> {
        ex.printStackTrace()
        val apiResponse = ApiResponse(
            status = HttpStatus.BAD_REQUEST.name,
            message = "Required request body is missing or unreadable",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(apiResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(FileSizeExceededException::class)
    fun handleFileSizeExceededException(
        ex: FileSizeExceededException
    ): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.PAYLOAD_TOO_LARGE.name,
            message = ex.message ?: "File size exceeded",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(apiResponse, HttpStatus.PAYLOAD_TOO_LARGE)
    }

    @ExceptionHandler(InvalidFileTypeException::class)
    fun handleInvalidFileTypeException(
        ex: InvalidFileTypeException
    ): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.BAD_REQUEST.name,
            message = ex.message ?: "Invalid file type",
            timestamp = System.currentTimeMillis()
        )
        return ResponseEntity(apiResponse, HttpStatus.BAD_REQUEST)
    }

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
            .message(ex.message)
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