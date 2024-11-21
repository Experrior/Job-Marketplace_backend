package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.requests.LinkRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.LinkResponse
import com.jobsearch.userservice.services.LinkService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import java.util.*

@Controller
class LinkController(
    private val linkService: LinkService
) {
    @MutationMapping
    fun addLink(
        @AuthenticationPrincipal userId: UUID,
        @Argument linkRequest: LinkRequest
        ): List<LinkResponse> {
        return linkService.addLink(userId, linkRequest)
    }

    @MutationMapping
    fun deleteLinkById(
        @AuthenticationPrincipal userId: UUID,
        @Argument linkId: UUID
        ): List<LinkResponse> {
        return linkService.deleteLink(userId, linkId)
    }

    @MutationMapping
    fun deleteAllLinks(
        @AuthenticationPrincipal userId: UUID
        ): DeleteResponse {
        return linkService.deleteAllLinks(userId)
    }

}