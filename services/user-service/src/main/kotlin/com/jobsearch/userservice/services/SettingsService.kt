package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Settings
import com.jobsearch.userservice.requests.SettingsRequest
import com.jobsearch.userservice.responses.DeleteResponse
import java.util.*

interface SettingsService {
    fun getSettingsByUserId(userId: UUID): Settings?
    fun createDefaultUserSettings(userId: UUID): Settings
    fun updateSettings(userId: UUID, settingsRequest: SettingsRequest): Settings
    fun deleteSettingsByUserId(userId: UUID): DeleteResponse
}