package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity(name = "employee_verification_tokens")
data class EmployeeVerificationToken(
    @Id
    @GeneratedValue
    @Column(name = "token_id", updatable = false, nullable = false)
    val tokenId: UUID? = null,
    @Column(nullable = false)
    var token: String = UUID.randomUUID().toString(),
    @Column(name = "expiry_date", nullable = false)
    var expiryDate: Date = calculateExpiryDate(),
    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    var user: User? = null,
    @OneToOne(targetEntity = Company::class, fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    var company: Company? = null
){
    companion object {
        private const val EXPIRATION = 24 * 60

        fun calculateExpiryDate(): Date {
            val cal = Calendar.getInstance()
            cal.time = Timestamp(cal.time.time)
            cal.add(Calendar.MINUTE, EXPIRATION)
            return Date(cal.time.time)
        }
    }
}