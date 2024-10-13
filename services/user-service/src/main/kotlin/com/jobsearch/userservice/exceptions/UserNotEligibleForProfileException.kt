package com.jobsearch.userservice.exceptions

import java.util.*

class UserNotEligibleForProfileException(val userId: UUID): RuntimeException() {
}