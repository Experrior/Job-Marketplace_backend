package com.jobsearch.jobservice.controllers

import com.jobsearch.jobservice.responses.QuizResponse
import com.jobsearch.jobservice.services.QuizService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
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
        @AuthenticationPrincipal userId: String,
        @RequestParam("quizConfig", required = true) quizConfig: MultipartFile
    ): ResponseEntity<QuizResponse> {
        return try {
            ResponseEntity(
                quizService.createQuiz(UUID.fromString(userId), quizConfig),
                HttpStatus.CREATED
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(null)
        }
    }

    @QueryMapping
    fun quizzesByRecruiter(
        @AuthenticationPrincipal userId: String
    ): List<QuizResponse> {
        return try {
            quizService.recruiterQuizzes(UUID.fromString(userId))
        } catch (e: IllegalArgumentException) {
            emptyList()
        }
    }

    @QueryMapping
    fun quizById(
        @Argument quizId: UUID
    ): QuizResponse {
        return quizService.findQuizById(quizId)
    }

}