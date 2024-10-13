package com.jobsearch.userservice.exceptions

class CompanyNotFoundException(val companyName: String): RuntimeException() {
}