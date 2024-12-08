package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.responses.DeleteResponse
import com.jobsearch.userservice.responses.ResumeResponse
import com.jobsearch.userservice.services.ResumeService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Controller
class ResumeController(
    private val resumeService: ResumeService
) {
    @PostMapping("/resume")
    fun addResume(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam("resume") resume: MultipartFile
    ): ResponseEntity<ResumeResponse> {
        return ResponseEntity(resumeService.addResume(userId, resume), HttpStatus.OK)
    }

    @PostMapping("/resume/remove")
    fun removeResume(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam("resumeId") resumeId: UUID
    ): ResponseEntity<DeleteResponse> {
        return ResponseEntity(resumeService.deleteResume(userId, resumeId), HttpStatus.OK)
    }


    @QueryMapping
    fun userResumes(@AuthenticationPrincipal userId: UUID): List<ResumeResponse> {
        return resumeService.userResumes(userId)
    }

    @QueryMapping
    fun resumeById(@AuthenticationPrincipal userId: UUID,
                      @Argument resumeId: UUID): ResumeResponse {
        return resumeService.getResumeById(userId, resumeId)
    }
}