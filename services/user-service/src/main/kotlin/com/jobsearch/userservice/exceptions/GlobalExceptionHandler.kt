package com.jobsearch.userservice.exceptions

import com.jobsearch.userservice.responses.ApiResponse
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import jakarta.validation.ConstraintViolationException
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(FailedToStoreFileException::class)
    fun handleFailedToStoreFileException(ex: FailedToStoreFileException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.name,
            message = ex.message ?: "Failed to store file"
        )
        return ResponseEntity(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(UserRegistrationException::class)
    fun handleUserRegistrationException(ex: UserRegistrationException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.valueOf(ex.statusCode).name,
            message = ex.message ?: "User registration error"
        )
        return ResponseEntity(apiResponse, HttpStatus.valueOf(ex.statusCode))
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(ex: InvalidCredentialsException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.UNAUTHORIZED.name,
            message = ex.message ?: "Invalid credentials"
        )
        return ResponseEntity(apiResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.NOT_FOUND.name,
            message = ex.message ?: "User not found"
        )
        return ResponseEntity(apiResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserNotVerifiedException::class)
    fun handleUserNotVerifiedException(ex: UserNotVerifiedException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.FORBIDDEN.name,
            message = "User not verified"
        )
        return ResponseEntity(apiResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.CONFLICT.name,
            message = ex.message ?: "User already exists"
        )
        return ResponseEntity(apiResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(EmployeeNotVerifiedException::class)
    fun handleEmployeeNotVerifiedException(ex: EmployeeNotVerifiedException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.UNAUTHORIZED.name,
            message = ex.message ?: "Employee not verified"
        )
        return ResponseEntity(apiResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(CompanyNotFoundException::class)
    fun handleCompanyNotFoundException(ex: CompanyNotFoundException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.NOT_FOUND.name,
            message = "Company not found: ${ex.companyName}"
        )
        return ResponseEntity(apiResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(CompanyAlreadyExistsException::class)
    fun handleCompanyAlreadyExistsException(ex: CompanyAlreadyExistsException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.CONFLICT.name,
            message = ex.message ?: "Company already exists"
        )
        return ResponseEntity(apiResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(CompanyNotVerifiedException::class)
    fun handleCompanyNotVerifiedException(ex: CompanyNotVerifiedException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.FORBIDDEN.name,
            message = ex.message ?: "Company not verified"
        )
        return ResponseEntity(apiResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.BAD_REQUEST.name,
            message = ex.message ?: "Invalid token"
        )
        return ResponseEntity(apiResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors = getErrors(ex)
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(FileSizeExceededException::class)
    fun handleFileSizeExceededException(ex: FileSizeExceededException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.PAYLOAD_TOO_LARGE.name,
            message = ex.message ?: "File size exceeded"
        )
        return ResponseEntity(apiResponse, HttpStatus.PAYLOAD_TOO_LARGE)
    }

    @ExceptionHandler(InvalidFileTypeException::class)
    fun handleInvalidFileTypeException(ex: InvalidFileTypeException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.BAD_REQUEST.name,
            message = ex.message ?: "Invalid file type"
        )
        return ResponseEntity(apiResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ProfileAlreadyExistsException::class)
    fun handleUserProfileAlreadyExistsException(ex: ProfileAlreadyExistsException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.CONFLICT.name,
            message = ex.message ?: "Profile already exists"
        )
        return ResponseEntity(apiResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ResumeNotFoundException::class)
    fun handleResumeNotFoundException(ex: ResumeNotFoundException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.NOT_FOUND.name,
            message = ex.message ?: "Resume not found"
        )
        return ResponseEntity(apiResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UnauthorizedAccessException::class)
    fun handleRestUnauthorizedAccessException(ex: UnauthorizedAccessException): ResponseEntity<ApiResponse> {
        val apiResponse = ApiResponse(
            status = HttpStatus.UNAUTHORIZED.name,
            message = ex.message ?: "Unauthorized access"
        )
        return ResponseEntity(apiResponse, HttpStatus.UNAUTHORIZED)
    }

    @GraphQlExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptionsForGraphql(ex: MethodArgumentNotValidException): GraphQLError {
        val errors = getErrors(ex)

        return GraphqlErrorBuilder.newError()
            .message("Validation error")
            .extensions(mapOf("errors" to errors))
            .build()
    }

    @GraphQlExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptionForGraphql(ex: ConstraintViolationException): GraphQLError {
        val errors: MutableMap<String, String?> = HashMap()

        ex.constraintViolations.forEach { violation ->
            val fieldName = violation.propertyPath.toString()
            val errorMessage = violation.message
            errors[fieldName] = errorMessage
        }

        return GraphqlErrorBuilder.newError()
            .message("Validation error")
            .extensions(mapOf("errors" to errors))
            .build()
    }

    @GraphQlExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundExceptionForGraphql(
        ex: UserNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(UserNotEligibleForProfileException::class)
    fun handleUserNotEligibleForProfileException(
        ex: UserNotEligibleForProfileException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message("User not eligible for profile creation: ${ex.userId}")
            .build()
    }

    @GraphQlExceptionHandler(ProfileNotFoundException::class)
    fun handleUserProfileNotFoundException(
        ex: ProfileNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(SettingsNotFoundException::class)
    fun handleSettingsNotFoundException(
        ex: SettingsNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(SettingsAlreadyExistException::class)
    fun handleSettingsAlreadyExistException(
        ex: SettingsAlreadyExistException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(EducationNotFoundException::class)
    fun handleEducationNotFoundException(
        ex: EducationNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(InvalidUUIDException::class)
    fun handleInvalidUUIDException(
        ex: InvalidUUIDException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(SkillNotFoundException::class)
    fun handleSkillNotFoundException(
        ex: SkillNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(LinkNotFoundException::class)
    fun handleLinkNotFoundException(
        ex: LinkNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(ExperienceNotFoundException::class)
    fun handleExperienceNotFoundException(
        ex: ExperienceNotFoundException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    @GraphQlExceptionHandler(UnauthorizedAccessException::class)
    fun handleUnauthorizedAccessException(
        ex: UnauthorizedAccessException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message(ex.message)
            .build()
    }

    private fun getErrors(ex: MethodArgumentNotValidException): Map<String, String?> {
        val errors: MutableMap<String, String?> = HashMap()

        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = if (error is FieldError) {
                error.field
            } else {
                (error as ObjectError).objectName
            }
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage
        }

        return errors
    }
}