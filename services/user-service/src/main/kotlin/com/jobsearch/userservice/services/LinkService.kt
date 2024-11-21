package com.jobsearch.userservice.services

import com.jobsearch.userservice.requests.LinkRequest
import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.LinkResponse
import java.util.*

interface LinkService {
    fun addLink(userId: UUID, linkRequest: LinkRequest): List<LinkResponse>
    fun deleteLink(userId: UUID, linkId: UUID): List<LinkResponse>
    fun deleteAllLinks(userId: UUID): DeleteResponse
}