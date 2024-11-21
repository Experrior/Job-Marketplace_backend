package com.jobsearch.jobservice.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.util.*

@Entity(name = "viewed_jobs")
@IdClass(UserJobId::class)
data class ViewedJob(
    @Id
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Id
    @Column(name = "job_id", nullable = false)
    val jobId: UUID,

    @Column(name = "view_count", nullable = false)
    var viewCount: Int
){
    constructor() : this(
        userId = UUID.randomUUID(),
        jobId = UUID.randomUUID(),
        viewCount = 0
    )
}
