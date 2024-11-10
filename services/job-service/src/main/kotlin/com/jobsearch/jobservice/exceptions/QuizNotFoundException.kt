package com.jobsearch.jobservice.exceptions

import java.util.*

class QuizNotFoundException(val jobId: UUID): RuntimeException() {
}