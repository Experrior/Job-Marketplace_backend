package com.jobsearch.jobservice.responses

data class FollowJobResponse(
    val success: Boolean,
    val message: String,
    val isFollowed: Boolean
)
