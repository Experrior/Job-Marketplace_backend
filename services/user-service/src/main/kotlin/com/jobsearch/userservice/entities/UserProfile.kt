package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "user_profiles")
data class UserProfile (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id", updatable = false, nullable = false)
    var profileId: UUID? = null,

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    var user: User = User(),

    @Column(name = "s3_resume_path", nullable = false)
    var s3ResumePath: String? = null,

    @Transient
    var resumeUrl: String? = null,

    @Column(name = "s3_picture_path", nullable = false)
    var s3ProfilePicturePath: String? = "",

    @Transient
    var profilePictureUrl: String? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Timestamp = Timestamp.from(Instant.now()),

    @Column(nullable = true)
    var updatedAt: Timestamp? = null
) {
    @PrePersist
    fun onCreate() {
        createdAt = Timestamp.from(Instant.now())
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}