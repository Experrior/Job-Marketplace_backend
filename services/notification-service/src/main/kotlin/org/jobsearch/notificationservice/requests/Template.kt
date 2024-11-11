package org.jobsearch.notificationservice.requests

enum class Template(val fileName: String)  {
    VERIFICATION_EMAIL("verification-email.html"),
    RESET_PASSWORD_EMAIL("reset-password-email.html"),
    EMPLOYEE_VERIFICATION_EMAIL("employee-verification-email.html"),
    VERIFICATION_APPROVED("employee-verification-confirm-email.html"),
    VERIFICATION_REJECTED("employee-verification-reject-email.html")
}