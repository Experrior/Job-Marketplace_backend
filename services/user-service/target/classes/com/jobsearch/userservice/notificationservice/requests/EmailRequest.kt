package org.jobsearch.notificationservice.requests

data class EmailRequest(
    val to: String,
    val message: String,
    val emailType: EmailType,
    val employeeDetails: EmployeeDetails? = null
)