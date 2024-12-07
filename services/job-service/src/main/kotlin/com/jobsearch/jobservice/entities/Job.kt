package com.jobsearch.jobservice.entities

import com.jobsearch.jobservice.entities.enums.EmploymentType
import com.jobsearch.jobservice.entities.enums.ExperienceLevel
import com.jobsearch.jobservice.entities.enums.WorkLocation
import com.jobsearch.jobservice.entities.quizzes.Quiz
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
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

    @Column(name = "job_description", nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Type(JsonType::class)
    @Column(name = "required_skills", columnDefinition = "jsonb", nullable = false)
    var requiredSkills: List<Skill>,

    @Column(name = "required_experience")
    var requiredExperience: Int? = null,

    @Column(name = "location", nullable = false)
    var location: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    var employmentType: EmploymentType? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "work_location")
    var workLocation: WorkLocation? = null,

    @Column(name = "salary")
    var salary: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    var experienceLevel: ExperienceLevel? = null,

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    var quiz: Quiz? = null,

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
        requiredSkills = emptyList(),
        requiredExperience = null,
        location = "",
    )

    @PrePersist
    fun onCreate() {
        createdAt = Timestamp.from(Instant.now())
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}
