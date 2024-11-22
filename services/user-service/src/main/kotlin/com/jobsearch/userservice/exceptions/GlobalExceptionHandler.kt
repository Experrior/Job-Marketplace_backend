package com.jobsearch.userservice.exceptions

import com.jobsearch.userservice.responses.ErrorResponse
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
    fun handleFailedToStoreFileException(ex: FailedToStoreFileException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = ex.message ?: "Failed to store file"
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(UserRegistrationException::class)
    fun handleUserRegistrationException(ex: UserRegistrationException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = ex.statusCode,
            message = ex.message ?: "User registration error"
        )
        return ResponseEntity(errorResponse, HttpStatus.valueOf(ex.statusCode))
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(ex: InvalidCredentialsException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            message = ex.message ?: "Invalid credentials"
        )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = ex.message ?: "User not found"
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserNotVerifiedException::class)
    fun handleUserNotVerifiedException(ex: UserNotVerifiedException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            message = "User not verified"
        )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            message = "User already exists"
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(EmployeeNotVerifiedException::class)
    fun handleEmployeeNotVerifiedException(ex: EmployeeNotVerifiedException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            message = ex.message ?: "Employee not verified"
        )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(CompanyNotFoundException::class)
    fun handleCompanyNotFoundException(ex: CompanyNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = "Company not found: ${ex.companyName}"
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(CompanyAlreadyExistsException::class)
    fun handleCompanyAlreadyExistsException(ex: CompanyAlreadyExistsException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            message = ex.message ?: "Company already exists"
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(CompanyNotVerifiedException::class)
    fun handleCompanyNotVerifiedException(ex: CompanyNotVerifiedException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            message = ex.message ?: "Company not verified"
        )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message ?: "Invalid token"
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = getErrors(ex)
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = "Validation error: $errors"
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(FileSizeExceededException::class)
    fun handleFileSizeExceededException(ex: FileSizeExceededException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.PAYLOAD_TOO_LARGE.value(),
            message = ex.message ?: "File size exceeded"
        )
        return ResponseEntity(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE)
    }

    @ExceptionHandler(InvalidFileTypeException::class)
    fun handleInvalidFileTypeException(ex: InvalidFileTypeException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            message = ex.message ?: "Invalid file type"
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ProfileAlreadyExistsException::class)
    fun handleUserProfileAlreadyExistsException(ex: ProfileAlreadyExistsException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            message = ex.message ?: "Profile already exists"
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ResumeNotFoundException::class)
    fun handleResumeNotFoundException(ex: ResumeNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = ex.message ?: "Resume not found"
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
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