package com.jobsearch.userservice.requests

import jakarta.validation.constraints.NotBlank

data class LinkRequest(
    @field:NotBlank(message = "Link name must not be blank")
    val name: String,

    @field:NotBlank(message = "URL must not be blank")
    val url: String,
)
