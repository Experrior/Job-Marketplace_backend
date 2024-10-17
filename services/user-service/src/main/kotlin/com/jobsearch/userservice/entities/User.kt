package com.jobsearch.userservice.entities

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "users")
class User(
    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    val userId: UUID = UUID.randomUUID(),
    @Column(nullable = false)
    var email: String = "",
    @Column(nullable = false)
    var firstName: String = "",
    @Column(nullable = false)
    var lastName: String = "",
    @Column(nullable = false)
    private var password: String = "",
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.APPLICANT,
    @Column(nullable = true)
    var companyId: UUID? = null,
    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var verificationToken: VerificationToken? = null,
    @Column(nullable = false)
    private var isEnabled: Boolean = false,
    @Column(nullable = false)
    var isEmailVerified: Boolean = false,
    @Column(nullable = false)
    var isEmployeeVerified: Boolean = false,
    @Column(nullable = false)
    var createdAt: Timestamp = Timestamp(0),
    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0)
    ): UserDetails {
    @PrePersist
    fun onCreate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        createdAt = currentTimestamp
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Timestamp.from(Instant.now())
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String {
        return this.email
    }
}