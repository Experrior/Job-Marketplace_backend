package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.quizzes.Quiz
import com.jobsearch.jobservice.exceptions.EmptyFileException
import com.jobsearch.jobservice.exceptions.FileSizeExceededException
import com.jobsearch.jobservice.exceptions.InvalidFileTypeException
import com.jobsearch.jobservice.exceptions.QuizNotFoundException
import com.jobsearch.jobservice.repositories.QuizRepository
import com.jobsearch.jobservice.responses.DeleteQuizResponse
import com.jobsearch.jobservice.responses.QuizResponse
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class QuizServiceImpl(
    private val quizRepository: QuizRepository,
    private val fileStorageService: FileStorageService,
): QuizService {
    override fun createQuiz(recruiterId: UUID, quizConfig: MultipartFile): QuizResponse {
        checkQuizConfigIsEmpty(quizConfig)
        checkQuizConfigSize(quizConfig)
        checkQuizConfigType(quizConfig)
        val s3QuizPath = fileStorageService.storeQuizConfig(recruiterId, quizConfig)

        val quiz = createQuizObject(s3QuizPath, recruiterId)
        val savedQuiz = quizRepository.save(quiz)
        return createQuizResponse(savedQuiz)
    }

    override fun recruiterQuizzes(recruiterId: UUID): List<QuizResponse> {
        return fileStorageService.listQuizConfigs(recruiterId)
    }

    override fun findQuizEntityById(quizId: UUID): Quiz {
        return quizRepository.findById(quizId).orElseThrow(
            { throw QuizNotFoundException("Quiz not found by id: $quizId") }
        )
    }

    override fun findQuizById(quizId: UUID): QuizResponse {
        val quiz = findQuizEntityById(quizId)
        return createQuizResponse(quiz)
    }

    override fun deleteQuizById(recruiterId: UUID, quizId: UUID): DeleteQuizResponse {
        val quiz = findQuizEntityById(quizId)
        if (quiz.recruiterId != recruiterId) {
            return DeleteQuizResponse(false, "Quiz does not belong to the recruiter")
        }
        quiz.isDeleted = true
        quizRepository.save(quiz)
        return DeleteQuizResponse(true, "Quiz deleted successfully")
    }

    override fun restoreQuizById(recruiterId: UUID, quizId: UUID): QuizResponse {
        val quiz = findQuizEntityById(quizId)
        if (quiz.recruiterId != recruiterId) {
            throw QuizNotFoundException("Quiz does not belong to the recruiter")
        }
        quiz.isDeleted = false
        val savedQuiz = quizRepository.save(quiz)
        return createQuizResponse(savedQuiz)
    }

    private fun createQuizResponse(savedQuiz: Quiz): QuizResponse {
        val s3QuizUrl = fileStorageService.getFileUrl(savedQuiz.s3QuizPath!!)
        val quizName = savedQuiz.s3QuizPath!!.substringAfterLast('/')

        return QuizResponse(
            quizId = savedQuiz.quizId!!,
            quizName = quizName,
            s3QuizUrl = s3QuizUrl,
            createdAt = savedQuiz.createdAt,
            isDeleted = savedQuiz.isDeleted
        )
    }

    private fun createQuizObject(s3QuizPath: String, recruiterId: UUID): Quiz {
        return Quiz(
            s3QuizPath = s3QuizPath,
            recruiterId = recruiterId,
            isDeleted = false
        )
    }

    private fun checkQuizConfigType(quizConfig: MultipartFile) {
        if (quizConfig.contentType != "application/json") {
            throw InvalidFileTypeException()
        }
    }

    private fun checkQuizConfigIsEmpty(quizConfig: MultipartFile) {
        if(quizConfig.isEmpty)
            throw EmptyFileException()
    }

    private fun checkQuizConfigSize(quizConfig: MultipartFile) {
        if (quizConfig.size > 1 * 1024 * 1024) {
            throw FileSizeExceededException()
        }
    }
}

