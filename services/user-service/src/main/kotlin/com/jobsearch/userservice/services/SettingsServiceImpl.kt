package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Settings
import com.jobsearch.userservice.exceptions.SettingsAlreadyExistException
import com.jobsearch.userservice.exceptions.SettingsNotFoundException
import com.jobsearch.userservice.repositories.SettingsRepository
import com.jobsearch.userservice.requests.SettingsRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class SettingsServiceImpl(
    private val settingsRepository: SettingsRepository,
    private val userService: UserService
): SettingsService {
    override fun getSettingsByUserId(userId: UUID): Settings? {
        val user = userService.getUserById(userId)
        return settingsRepository.findByUser(user)
    }

    override fun createSettings(
        userId: UUID,
        settingsRequest: SettingsRequest
    ): Settings {
        val user = userService.getUserById(userId)

        if(settingsRepository.existsByUser(user))
            throw SettingsAlreadyExistException("Settings already exist for user with id: $userId")

        val settings = Settings(
            user = user,
            offersNotification = settingsRequest.offersNotification,
            newsletterNotification = settingsRequest.newsletterNotification,
            recruiterMessages = settingsRequest.recruiterMessages,
            pushNotification = settingsRequest.pushNotification
        )

        return settingsRepository.save(settings)
    }

    override fun updateSettings(
        userId: UUID,
        settingsRequest: SettingsRequest
    ): Settings {
        val settings = getSettingsByUserId(userId)
            ?: throw SettingsNotFoundException("Settings not found for user with id: $userId")

        settingsRequest.offersNotification.let { settings.offersNotification = it }
        settingsRequest.newsletterNotification.let { settings.newsletterNotification = it }
        settingsRequest.recruiterMessages.let { settings.recruiterMessages = it }
        settingsRequest.pushNotification.let { settings.pushNotification = it }

        return settingsRepository.save(settings)
    }

    override fun deleteSettingsByUserId(userId: UUID): Boolean {
        return try {
            val settings = getSettingsByUserId(userId) ?: throw SettingsNotFoundException("Settings not found for user with id: $userId")
            settingsRepository.delete(settings)
            true
        } catch (e: Exception) {
            false
        }
    }
}