package com.jobsearch.userservice.replica_repositories

import com.jobsearch.userservice.entities.Settings
import com.jobsearch.userservice.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SettingsRepositoryReplica: JpaRepository<Settings, UUID> {
    fun findByUser(user: User): Settings?
    fun existsByUser(user: User): Boolean
}