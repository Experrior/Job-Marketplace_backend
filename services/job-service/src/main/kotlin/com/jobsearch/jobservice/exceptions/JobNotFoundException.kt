package com.jobsearch.jobservice.exceptions

import java.util.*

class JobNotFoundException(val jobId: UUID): RuntimeException() {
}