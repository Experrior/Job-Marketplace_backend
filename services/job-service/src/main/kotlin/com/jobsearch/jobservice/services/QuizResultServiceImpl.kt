package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.QuizResult
import com.jobsearch.jobservice.exceptions.QuizResultNotFoundException
import com.jobsearch.jobservice.repositories.QuizResultRepository
import com.jobsearch.jobservice.requests.QuizResultRequest
import com.jobsearch.jobservice.responses.QuizResultResponse
import org.springframework.stereotype.Service
import java.util.*

@Service
class QuizResultServiceImpl(
    private val quizResultRepository: QuizResultRepository,
    private val quizService: QuizService,
): QuizResultService {
    override fun saveQuizResult(quizResultRequest: QuizResultRequest, userId: UUID): QuizResultResponse {
        val quizResult = quizResultRepository.save(mapQuizResultRequestToQuizResult(quizResultRequest, userId))

        return mapQuizResultToResponse(quizResult)
    }

    override fun getQuizResultEntityById(quizResultId: UUID): QuizResult {
        return quizResultRepository.findById(quizResultId)
            .orElseThrow { QuizResultNotFoundException("Quiz result not found by id: $quizResultId") }
    }

    private fun mapQuizResultRequestToQuizResult(quizResultRequest: QuizResultRequest, userId: UUID): QuizResult {
        return QuizResult(
            quiz = quizService.findQuizEntityById(quizResultRequest.quizId),
            applicantId = userId,
            score = quizResultRequest.score,
            timeTaken = quizResultRequest.timeTaken
        )
    }

    override fun mapQuizResultToResponse(quizResult: QuizResult): QuizResultResponse {
        return QuizResultResponse(
            quizResultId = quizResult.quizResultId!!,
            quizId = quizResult.quiz.quizId!!,
            applicantId = quizResult.applicantId,
            score = quizResult.score,
            timeTaken = quizResult.timeTaken
        )
    }
}