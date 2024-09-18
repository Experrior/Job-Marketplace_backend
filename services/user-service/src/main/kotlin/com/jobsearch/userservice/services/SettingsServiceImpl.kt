package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Settings
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.repositories.SettingsRepository

class SettingsServiceImpl(
    private val settingsRepository: SettingsRepository
): SettingsService {
    override fun getSettingsByUser(user: User): Settings? {
        return settingsRepository.findByUser(user)
    }
}