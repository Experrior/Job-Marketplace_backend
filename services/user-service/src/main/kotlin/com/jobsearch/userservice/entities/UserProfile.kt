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

    @OneToMany(mappedBy = "userProfile", cascade = [CascadeType.ALL], orphanRemoval = true)
    var resumes: MutableList<Resume> = mutableListOf(),

    @OneToMany(mappedBy = "userProfile", cascade = [CascadeType.ALL], orphanRemoval = true)
    var skills: MutableList<Skill> = mutableListOf(),

    @OneToMany(mappedBy = "userProfile", cascade = [CascadeType.ALL], orphanRemoval = true)
    var experiences: MutableList<Experience> = mutableListOf(),

    @OneToMany(mappedBy = "userProfile", cascade = [CascadeType.ALL], orphanRemoval = true)
    var educations: MutableList<Education> = mutableListOf(),

    @OneToMany(mappedBy = "userProfile", cascade = [CascadeType.ALL], orphanRemoval = true)
    var links: MutableList<UserLink> = mutableListOf(),

    @Column(name = "s3_picture_path", nullable = true)
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