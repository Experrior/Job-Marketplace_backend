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
        @AuthenticationPrincipal userId: String
    ): Settings? {
        return settingsService.getSettingsByUserId(UUID.fromString(userId))
    }

    @MutationMapping
    fun createUserSettings(
        @AuthenticationPrincipal userId: String,
        @Argument @Valid settingsRequest: SettingsRequest
    ): Settings? {
        return settingsService.createSettings(UUID.fromString(userId), settingsRequest)
    }

    @MutationMapping
    fun updateCurrentUserSettings(
        @AuthenticationPrincipal userId: String,
        @Argument @Valid settingsRequest: SettingsRequest
    ): Settings? {
        return settingsService.updateSettings(UUID.fromString(userId), settingsRequest)
    }

    @MutationMapping
    fun deleteCurrentUserSettings(
        @AuthenticationPrincipal userId: String
    ): Boolean {
        return settingsService.deleteSettingsByUserId(UUID.fromString(userId))
    }
}