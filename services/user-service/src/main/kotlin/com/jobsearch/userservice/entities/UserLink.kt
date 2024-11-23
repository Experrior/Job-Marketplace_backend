package com.jobsearch.userservice.entities

import jakarta.persistence.*
import java.util.*

@Entity(name = "user_links")
data class UserLink(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "link_id", updatable = false, nullable = false)
    var linkId: UUID? = null,

    @Column(name = "link_name", nullable = false)
    val name: String = "",

    @Column(name = "link_url", nullable = false)
    val url: String = "",

    @ManyToOne
    @JoinColumn(name = "profile_id", referencedColumnName = "profile_id", nullable = false)
    var userProfile: UserProfile,
) {
    constructor() : this(
        name = "",
        url = "",
        userProfile = UserProfile()
    )
}
