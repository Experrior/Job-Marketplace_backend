package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Settings
import com.jobsearch.userservice.entities.User

interface SettingsService {
    fun getSettingsByUser(user: User): Settings?
}