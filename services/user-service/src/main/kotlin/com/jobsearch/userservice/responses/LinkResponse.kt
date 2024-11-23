package com.jobsearch.userservice.responses

import java.util.*

data class LinkResponse(
    val linkId: UUID,
    val name: String,
    val url: String,
)
