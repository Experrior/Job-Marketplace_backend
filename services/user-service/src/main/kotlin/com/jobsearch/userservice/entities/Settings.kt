package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "user_settings")
class Settings() {  // Primary no-arg constructor

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "settingsId", updatable = false, nullable = false)
    var settingsId: UUID? = null

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    lateinit var user: User

    @Column(nullable = false)
    var offersNotification: Boolean = false

    @Column(nullable = false)
    var newsletterNotification: Boolean = false

    @Column(nullable = false)
    var recruiterMessages: Boolean = false

    @Column(nullable = false)
    var pushNotification: Boolean = false

    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)

    constructor(
        user: User,
        offersNotification: Boolean,
        newsletterNotification: Boolean,
        recruiterMessages: Boolean,
        pushNotification: Boolean
    ) : this() {
        this.user = user
        this.offersNotification = offersNotification
        this.newsletterNotification = newsletterNotification
        this.recruiterMessages = recruiterMessages
        this.pushNotification = pushNotification
        this.updatedAt = Timestamp.from(Instant.now())
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }
}
