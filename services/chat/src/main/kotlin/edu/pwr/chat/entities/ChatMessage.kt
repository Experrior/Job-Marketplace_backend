package edu.pwr.chat.entities

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import java.sql.Timestamp
import java.time.Instant

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "chats")
class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var chatId: Int? = null,

    @Column(nullable = false)
    var content: String = "",

    @Column
    @ElementCollection
    var members: List<Int> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    var createdBy: User = User(),

    @Column
    @ElementCollection
    var deletedBy: List<Int> = mutableListOf(),

    @Column
    var lastMessage: String = "",

    @Column
    @ElementCollection
    var tags: List<String> = mutableListOf(),

    @Column(nullable = false)
    var createdAt: Timestamp = Timestamp(0),

    @Column(nullable = false)
    var updatedAt: Timestamp = Timestamp(0),
) {

    @PrePersist
    fun onCreate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        updatedAt = currentTimestamp
        createdAt = currentTimestamp
    }

    @PreUpdate
    fun onUpdate() {
        val currentTimestamp = Timestamp.from(Instant.now())
        updatedAt = currentTimestamp
    }


}
