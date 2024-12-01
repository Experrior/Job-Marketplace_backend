package com.jobsearch.jobservice.responses

data class ApiResponse(
    val message: String,
    val status: String = "success",
    val timestamp: Long = System.currentTimeMillis()
)
