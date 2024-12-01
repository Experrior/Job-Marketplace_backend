package com.jobsearch.jobservice.services

import java.util.*

interface UserServiceUtils {
    fun getRecruiterCompany(): UUID
    fun getApplicantFullName(userId: UUID): String
    fun getCompanyName(companyId: UUID): String
    fun getS3ResumePath(resumeId: UUID): String
}