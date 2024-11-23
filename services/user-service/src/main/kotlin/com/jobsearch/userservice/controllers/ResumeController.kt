package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.responses.ResumeResponse
import com.jobsearch.userservice.services.ResumeService
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
    ): ResponseEntity<List<ResumeResponse>> {
        return ResponseEntity(resumeService.addResume(userId, resume), HttpStatus.OK)
    }

    @PostMapping("/resume/remove")
    fun removeResume(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam("resumeId") resumeId: UUID
    ): ResponseEntity<List<ResumeResponse>> {
        return ResponseEntity(resumeService.deleteResume(userId, resumeId), HttpStatus.OK)
    }
}