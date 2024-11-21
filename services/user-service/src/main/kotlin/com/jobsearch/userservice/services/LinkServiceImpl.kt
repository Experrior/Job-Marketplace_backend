package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.UserLink
import com.jobsearch.userservice.exceptions.LinkNotFoundException
import com.jobsearch.userservice.exceptions.UnauthorizedAccessException
import com.jobsearch.userservice.repositories.LinkRepository
import com.jobsearch.userservice.requests.LinkRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.LinkResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class LinkServiceImpl(
    private val linkRepository: LinkRepository,
    private val userProfileService: UserProfileService,
    private val mapper: UserProfileMapper
): LinkService {
    @Transactional
    override fun addLink(userId: UUID, linkRequest: LinkRequest): List<LinkResponse> {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)

        val newLink = UserLink(
            userProfile = profile,
            name = linkRequest.name,
            url = linkRequest.url
        )

        linkRepository.save(newLink)
        return linkRepository.findByUserProfile(profile).map { mapper.toLinkResponse(it) }
    }

    @Transactional
    override fun deleteLink(userId: UUID, linkId: UUID): List<LinkResponse> {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)
        val linkToRemove = getLinkById(linkId)

        if (linkToRemove.userProfile != profile) {
            throw UnauthorizedAccessException("Link does not belong to the user's profile")
        }

        linkRepository.delete(linkToRemove)
        return linkRepository.findByUserProfile(profile).map { mapper.toLinkResponse(it) }
    }

    @Transactional
    override fun deleteAllLinks(userId: UUID): DeleteResponse {
        val profile = userProfileService.getUserProfileEntityByUserId(userId)
        val links = linkRepository.findByUserProfile(profile)

        linkRepository.deleteAll(links)
        return DeleteResponse(
            success = true,
            message = "All links deleted successfully"
        )
    }

    private fun getLinkById(linkId: UUID): UserLink {
        return linkRepository.findById(linkId)
            .orElseThrow { LinkNotFoundException("Link not found with id: $linkId") }
    }
}