package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "user_settings")
class Settings(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "settings_id", updatable = false, nullable = false)
    var settingsId: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    var user: User,

    @Column(name = "offers_notification", nullable = false)
    var offersNotification: Boolean = false,

    @Column(name = "newsletter_notification", nullable = false)
    var newsletterNotification: Boolean = false,

    @Column(name = "recruiter_messages", nullable = false)
    var recruiterMessages: Boolean = false,

    @Column(name = "push_notification", nullable = false)
    var pushNotification: Boolean = false,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
) {
    protected constructor() : this(
        user = User(),
        offersNotification = false,
        newsletterNotification = false,
        recruiterMessages = false,
        pushNotification = false
    )

    @PrePersist
    fun onCreate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        updatedAt = currentTimestamp
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}
