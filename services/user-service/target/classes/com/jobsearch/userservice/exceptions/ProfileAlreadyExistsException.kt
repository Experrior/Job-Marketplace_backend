package com.jobsearch.userservice.exceptions

import java.util.*

class ProfileAlreadyExistsException(val userId: UUID): RuntimeException() {
}