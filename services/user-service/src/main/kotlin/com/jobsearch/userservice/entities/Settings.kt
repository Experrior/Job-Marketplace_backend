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
    @Column(nullable = false)
    var offersNotification: Boolean = false,
    @Column(nullable = false)
    var newsletterNotification: Boolean = false,
    @Column(nullable = false)
    var recruiterMessages: Boolean = false,
    @Column(nullable = false)
    var pushNotification: Boolean = false,
    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
){
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
}
