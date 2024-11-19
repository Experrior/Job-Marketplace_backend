package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.Settings
import com.jobsearch.userservice.requests.SettingsRequest
import com.jobsearch.userservice.services.SettingsService
import jakarta.validation.Valid
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class SettingsController(
    private val settingsService: SettingsService
) {
    @QueryMapping
    fun currentUserSettings(
        @AuthenticationPrincipal userId: UUID
    ): Settings? {
        return settingsService.getSettingsByUserId(userId)
    }

    @MutationMapping
    fun updateCurrentUserSettings(
        @AuthenticationPrincipal userId: UUID,
        @Argument @Valid settingsRequest: SettingsRequest
    ): Settings? {
        return settingsService.updateSettings(userId, settingsRequest)
    }

    @MutationMapping
    fun deleteCurrentUserSettings(
        @AuthenticationPrincipal userId: UUID
    ): Boolean {
        return settingsService.deleteSettingsByUserId(userId)
    }
}