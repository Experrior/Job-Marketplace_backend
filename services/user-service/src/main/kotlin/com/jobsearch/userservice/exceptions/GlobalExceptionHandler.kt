package com.jobsearch.userservice.exceptions

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus


@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(UserRegistrationException::class)
    fun handleUserRegistrationException(
        ex: UserRegistrationException
    ): ResponseEntity<String> {
        return ResponseEntity(
            ex.message,
            HttpStatusCode.valueOf(ex.statusCode)
        )
    }
    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(
        ex: InvalidCredentialsException
    ): ResponseEntity<String> {
        return ResponseEntity(
            ex.message,
            HttpStatus.UNAUTHORIZED
        )
    }
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        ex: UserNotFoundException
    ): ResponseEntity<String> {
        return ResponseEntity(
            ex.message,
            HttpStatus.NOT_FOUND
        )
    }
    @ExceptionHandler(UserNotVerifiedException::class)
    fun handleUserNotVerifiedException(
        ex: UserNotVerifiedException
    ): ResponseEntity<String> {
        return ResponseEntity(
            "User not verified",
            HttpStatus.FORBIDDEN
        )
    }
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(
        ex: UserAlreadyExistsException
    ): ResponseEntity<String> {
        return ResponseEntity(
            "User already exists",
            HttpStatus.CONFLICT
        )
    }
    @ExceptionHandler(CompanyNotFoundException::class)
    fun handleCompanyNotFoundException(
        ex: CompanyNotFoundException
    ): ResponseEntity<String> {
        return ResponseEntity(
            "Company not found: ${ex.companyName}",
            HttpStatus.NOT_FOUND
        )
    }
    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(
        ex: InvalidTokenException
    ): ResponseEntity<String> {
        return ResponseEntity(
            ex.message,
            HttpStatus.BAD_REQUEST
        )
    }
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors: MutableMap<String, String?> = HashMap()

        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage
        }

        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
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
    @GraphQlExceptionHandler(ProfileAlreadyExistsException::class)
    fun handleUserProfileAlreadyExistsException(
        ex: ProfileAlreadyExistsException
    ): GraphQLError {
        return GraphqlErrorBuilder.newError()
            .message("User profile already exists ${ex.userId}")
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
}