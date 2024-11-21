package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import kotlin.jvm.Transient

@Entity(name = "resumes")
data class Resume(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "resume_id", updatable = false, nullable = false)
    var resumeId: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id")
    var userProfile: UserProfile? = null,

    @Column(name = "resume_name", nullable = false)
    var resumeName: String = "",

    @Column(name = "s3_resume_path", nullable = true)
    var s3ResumePath: String? = null,

    @Transient
    var resumeUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Timestamp = Timestamp.from(Instant.now()),
) {
    @PrePersist
    fun onCreate() {
        createdAt = Timestamp.from(Instant.now())
    }
}
