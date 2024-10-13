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
    @Column(nullable = false)
    var skillName: String = "",
    @Column(nullable = false)
    var proficiencyLevel: String = "Intermediate",
    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
){
    protected constructor() : this(
        userProfile = UserProfile(),
        skillName = "",
        proficiencyLevel = "Intermediate",
        updatedAt = Timestamp(0)
    )

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}
