package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity(name = "reset_password_tokens")
data class ResetPasswordToken(
    @Id
    @GeneratedValue
    @Column(name = "token_id", updatable = false, nullable = false)
    val tokenId: UUID? = null,
    @Column(nullable = false)
    var token: String = UUID.randomUUID().toString(),
    @Column(nullable = false)
    var expiryDate: Date = calculateExpiryDate(),
    @OneToOne(targetEntity = User::class, fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name = "user_id")
    var user: User? = null
) {
    companion object {
        private const val EXPIRATION = 24 * 60

        fun calculateExpiryDate(): Date {
            val cal = Calendar.getInstance()
            cal.time = Timestamp(cal.time.time)
            cal.add(Calendar.MINUTE, EXPIRATION)
            return Date(cal.time.time)
        }
    }

    fun isExpired(): Boolean {
        return expiryDate.before(Date())
    }
}
