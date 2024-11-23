package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.responses.DeleteQuizResponse
import com.jobsearch.jobservice.responses.QuizResponse
import com.jobsearch.jobservice.services.QuizService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Controller
@RequestMapping("/quizzes")
class QuizController(
    private val quizService: QuizService
) {
    @PostMapping("/createQuiz", consumes = ["multipart/form-data"])
    fun createQuiz(
        @AuthenticationPrincipal recruiterId: UUID,
        @RequestParam("quizConfig", required = true) quizConfig: MultipartFile
    ): ResponseEntity<QuizResponse> {
        return ResponseEntity(
                quizService.createQuiz(recruiterId, quizConfig),
                HttpStatus.CREATED
            )
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @QueryMapping
    fun quizzesByRecruiter(
        @AuthenticationPrincipal recruiterId: UUID
    ): List<QuizResponse> {
        return quizService.getRecruiterQuizzes(recruiterId)
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @QueryMapping
    fun activeQuizzesByRecruiter(
        @AuthenticationPrincipal recruiterId: UUID
    ): List<QuizResponse> {
        return quizService.getActiveQuizzesByRecruiter(recruiterId)
    }

    @QueryMapping
    fun quizById(
        @Argument quizId: UUID
    ): QuizResponse {
        return quizService.findQuizById(quizId)
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun deleteQuiz(
        @AuthenticationPrincipal userId: UUID,
        @Argument quizId: UUID
    ): DeleteQuizResponse {
        return quizService.deleteQuizById(userId, quizId)
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @MutationMapping
    fun restoreQuiz(
        @AuthenticationPrincipal userId: UUID,
        @Argument quizId: UUID
    ): QuizResponse {
        return quizService.restoreQuizById(userId, quizId)
    }

    @PostMapping("/updateQuiz/{quizId}", consumes = ["multipart/form-data"])
    @PreAuthorize("hasRole('RECRUITER')")
    fun updateQuiz(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable quizId: UUID,
        @RequestParam("quizConfig", required = true) quizConfig: MultipartFile
    ): QuizResponse {
        return quizService.updateQuiz(userId, quizId, quizConfig)
    }

}