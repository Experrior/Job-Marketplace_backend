package com.jobsearch.userservice.entities

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@Entity(name = "app_users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    val userId: UUID? = null,
    @Column(nullable = false)
    var email: String = "",
    @Column(name = "first_name",nullable = false)
    var firstName: String = "",
    @Column(name = "last_name",nullable = false)
    var lastName: String = "",
    @Column(nullable = false)
    private var password: String = "",
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.APPLICANT,
    @Column(name = "company_id", nullable = true)
    var companyId: UUID? = null,
    @OneToOne(mappedBy = "user", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var verificationToken: VerificationToken? = null,
    @Column(name = "is_enabled",nullable = false)
    private var isEnabled: Boolean = false,
    @Column(name = "is_email_verified",nullable = false)
    var isEmailVerified: Boolean = false,
    @Column(name = "is_employee_verified", nullable = false)
    var isEmployeeVerified: Boolean = false,
    @Column(name = "created_at", nullable = false)
    var createdAt: Timestamp = Timestamp(0),
    @Column(name = "updated_at", nullable = false)
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

    fun setPassword(newPassword: String){
        this.password = newPassword
    }
}
