package com.jobsearch.userservice.repositories

import com.jobsearch.userservice.entities.Settings
import com.jobsearch.userservice.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SettingsRepository: JpaRepository<Settings, UUID> {
    fun findByUser(user: User): Settings?
    fun existsByUser(user: User): Boolean
}