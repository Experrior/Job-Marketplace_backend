package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "user_skills")
class Skill(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "skill_id", updatable = false, nullable = false)
    var skillId: UUID? = null,
    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id", nullable = false)
    var userProfile: UserProfile,

    @Column(name = "skill_name", nullable = false)
    var skillName: String = "",

    @Column(name = "proficiency_level", nullable = false)
    var proficiencyLevel: Int = 0,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
) {
    constructor() : this(
        userProfile = UserProfile(),
        skillName = "",
        proficiencyLevel = 0
    )

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}
