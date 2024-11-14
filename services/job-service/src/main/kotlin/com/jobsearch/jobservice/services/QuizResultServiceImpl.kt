package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.QuizResult
import com.jobsearch.jobservice.repositories.QuizResultRepository
import com.jobsearch.jobservice.requests.QuizResultRequest
import com.jobsearch.jobservice.responses.QuizResultResponse
import org.springframework.stereotype.Service
import java.util.*

@Service
class QuizResultServiceImpl(
    private val quizResultRepository: QuizResultRepository,
    private val quizService: QuizService,
    private val jobService: JobService
): QuizResultService {
    override fun saveQuizResult(quizResultRequest: QuizResultRequest, userId: UUID): QuizResultResponse {
        val quizResult = quizResultRepository.save(mapQuizResultRequestToQuizResult(quizResultRequest, userId))

        return mapQuizResultToQuizResultResponse(quizResult, userId)
    }

    private fun mapQuizResultRequestToQuizResult(quizResultRequest: QuizResultRequest, userId: UUID): QuizResult {
        return QuizResult(
            quiz = quizService.findQuizEntityById(quizResultRequest.quizId),
            applicantId = userId,
            score = quizResultRequest.score,
            timeTaken = quizResultRequest.timeTaken
        )
    }

    private fun mapQuizResultToQuizResultResponse(quizResult: QuizResult, userId: UUID): QuizResultResponse {
        return QuizResultResponse(
            quizResultId = quizResult.quizResultId!!,
            quizId = quizResult.quiz.quizId!!,
            applicantId = quizResult.applicantId,
            score = quizResult.score,
            timeTaken = quizResult.timeTaken
        )
    }
}