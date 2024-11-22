package com.jobsearch.userservice.requests

import com.fasterxml.jackson.annotation.JsonProperty

data class CompanyRegistrationRequest(
    @JsonProperty("email") val email: String,

    @JsonProperty("companyName") val companyName: String,

    @JsonProperty("industry") val industry: String,

    @JsonProperty("description") val description: String?,
)