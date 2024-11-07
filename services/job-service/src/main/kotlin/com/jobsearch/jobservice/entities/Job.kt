package com.jobsearch.jobservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "jobs")
data class Job(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "job_id", updatable = false, nullable = false)
    var jobId: UUID? = null,

    @Column(name = "recruiter_id", nullable = false)
    var recruiterId: UUID,

    @Column(name = "company_id", nullable = false)
    var companyId: UUID,

    @Column(name = "job_title", nullable = false)
    var title: String,

    @Column(name = "job_description", nullable = false)
    var description: String,

    @Column(name = "required_skills", nullable = false)
    var requiredSkills: String,

    @Column(name = "required_experience", nullable = false)
    var requiredExperience: String,

    @Column(name = "location", nullable = false)
    var location: String,

    @Column(name = "salary")
    var salary: Int? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Timestamp = Timestamp.from(Instant.now()),

    @Column(name = "updated_at", nullable = true)
    var updatedAt: Timestamp? = null,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false
) {
    constructor() : this(
        companyId = UUID.randomUUID(),
        recruiterId = UUID.randomUUID(),
        title = "",
        description = "",
        requiredSkills = "",
        requiredExperience = "",
        location = ""
    ){

    }

    @PrePersist
    fun onCreate() {
        createdAt = Timestamp.from(Instant.now())
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}
