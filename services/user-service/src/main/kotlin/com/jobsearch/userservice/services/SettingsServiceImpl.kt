package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Settings
import com.jobsearch.userservice.exceptions.SettingsNotFoundException
import com.jobsearch.userservice.repositories.SettingsRepository
import com.jobsearch.userservice.requests.SettingsRequest
import com.jobsearch.userservice.responses.DeleteResponse
import org.springframework.stereotype.Service
import java.util.*

@Service
class SettingsServiceImpl(
    private val settingsRepository: SettingsRepository,
    private val userService: UserService
): SettingsService {
    override fun getSettingsByUserId(userId: UUID): Settings {
        val user = userService.getUserById(userId)
        return settingsRepository.findByUser(user) ?: throw SettingsNotFoundException("Settings not found for user with id: $userId")
    }

    override fun createDefaultUserSettings(
        userId: UUID,
    ): Settings {
        val user = userService.getUserById(userId)

        val settings = Settings(
            user = user,
            offersNotification = true,
            newsletterNotification = true,
            recruiterMessages = true,
            pushNotification = true
        )

        return settingsRepository.save(settings)
    }

    override fun updateSettings(
        userId: UUID,
        settingsRequest: SettingsRequest
    ): Settings {
        val settings = getSettingsByUserId(userId)

        settingsRequest.offersNotification.let { settings.offersNotification = it }
        settingsRequest.newsletterNotification.let { settings.newsletterNotification = it }
        settingsRequest.recruiterMessages.let { settings.recruiterMessages = it }
        settingsRequest.pushNotification.let { settings.pushNotification = it }

        return settingsRepository.save(settings)
    }

    override fun deleteSettingsByUserId(userId: UUID): DeleteResponse {
        return try {
            val settings = getSettingsByUserId(userId)
            settingsRepository.delete(settings)
            DeleteResponse(
                success = true,
                message = "Settings deleted successfully"
            )
        } catch (e: Exception) {
            DeleteResponse(
                success = false,
                message = e.message ?: "An error occurred while deleting settings"
            )
        }
    }
}