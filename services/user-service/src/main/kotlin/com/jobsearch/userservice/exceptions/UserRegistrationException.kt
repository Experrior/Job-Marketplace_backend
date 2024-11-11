package com.jobsearch.userservice.exceptions

class UserRegistrationException(message: String, val statusCode: Int) : RuntimeException(message)
