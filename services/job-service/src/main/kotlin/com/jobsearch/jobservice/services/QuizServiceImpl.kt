package com.jobsearch.jobservice.services

import com.jobsearch.jobservice.entities.quizzes.Quiz
import com.jobsearch.jobservice.exceptions.EmptyFileException
import com.jobsearch.jobservice.exceptions.FileSizeExceededException
import com.jobsearch.jobservice.exceptions.InvalidFileTypeException
import com.jobsearch.jobservice.repositories.QuizRepository
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

    private fun createQuizResponse(savedQuiz: Quiz): QuizResponse {
        val s3QuizUrl = fileStorageService.getFileUrl(savedQuiz.s3QuizPath!!)

        return QuizResponse(
            quizId = savedQuiz.quizId!!,
            quizName = savedQuiz.s3QuizPath!!,
            s3QuizUrl = s3QuizUrl
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

