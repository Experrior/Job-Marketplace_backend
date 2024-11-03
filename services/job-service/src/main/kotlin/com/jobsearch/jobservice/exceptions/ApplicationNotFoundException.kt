package com.jobsearch.jobservice.exceptions

import java.util.*

class ApplicationNotFoundException(val applicationId: UUID): RuntimeException() {
}