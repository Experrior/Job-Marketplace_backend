package com.jobsearch.userservice.entities
import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "skills")
class Skill() {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "skillId", updatable = false, nullable = false)
    var skillId: UUID? = null

    @ManyToOne
    @JoinColumn(name = "profileId", referencedColumnName = "profileId", nullable = false)
    lateinit var profile: Profile

    @Column(nullable = false)
    var skillName: String = ""

    @Column(nullable = false)
    var proficiencyLevel: String = "Intermediate"

    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }

    constructor(profile: Profile, skillName: String, proficiencyLevel: String) : this() {
        this.profile = profile
        this.skillName = skillName
        this.proficiencyLevel = proficiencyLevel
    }
}
