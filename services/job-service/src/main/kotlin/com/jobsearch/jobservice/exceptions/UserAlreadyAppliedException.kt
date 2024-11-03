package com.jobsearch.jobservice.exceptions

import java.util.*

class UserAlreadyAppliedException(userId: UUID, jobId: UUID): RuntimeException("User $userId has already applied for job $jobId") {
}