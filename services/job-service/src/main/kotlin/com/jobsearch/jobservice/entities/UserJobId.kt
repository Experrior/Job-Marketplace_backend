package com.jobsearch.jobservice.entities

import java.io.Serializable
import java.util.*

data class UserJobId(
    var userId: UUID? = null,
    var jobId: UUID? = null
) : Serializable

