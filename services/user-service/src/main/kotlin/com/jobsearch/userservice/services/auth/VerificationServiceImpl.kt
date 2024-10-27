package com.jobsearch.userservice.services.auth

import com.jobsearch.messagequeue.RabbitMQMessageProducer
import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.entities.User
import com.jobsearch.userservice.exceptions.CompanyNotFoundException
import com.jobsearch.userservice.exceptions.InvalidTokenException
import com.jobsearch.userservice.exceptions.UserNotFoundException
import com.jobsearch.userservice.services.CompanyService
import com.jobsearch.userservice.services.UserService
import com.jobsearch.userservice.services.auth.tokens.VerificationTokenService
import org.jobsearch.notificationservice.requests.EmailRequest
import org.jobsearch.notificationservice.requests.EmailType
import org.jobsearch.notificationservice.requests.EmployeeDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class VerificationServiceImpl(
    private val messageProducer: RabbitMQMessageProducer,
    private val verificationTokenService: VerificationTokenService,
    private val userService: UserService,
    private val companyService: CompanyService
): VerificationService {

    override fun sendVerificationEmail(user: User) {
        val verificationToken = verificationTokenService.generateVerificationToken(user)

        messageProducer.publish(
            generateEmailRequest(user.email, verificationToken.token),
            "internal.exchange",
            "internal.email.routing-key"
        )
    }

    override fun sendVerificationEmail(company: Company) {
        val verificationToken = verificationTokenService.generateVerificationToken(company)

        messageProducer.publish(
            generateEmailRequest(company.email, verificationToken.token),
            "internal.exchange",
            "internal.email.routing-key"
        )
    }

    override fun sendEmployeeVerificationEmail(recruiter: User, companyEmail: String) {
        val employeeVerificationToken = verificationTokenService.generateEmployeeVerificationToken(recruiter)
        val employeeDetails = generateEmployeeDetails(recruiter)

        messageProducer.publish(
            generateEmployeeEmailRequest(companyEmail, employeeVerificationToken.token, employeeDetails),
            "internal.exchange",
            "internal.email.routing-key"
        )
    }

    private fun getCompanyFromToken(token: String): Company {
        val verificationToken = verificationTokenService.getVerificationToken(token)
        validateTokenExpiry(verificationToken.expiryDate)
        return verificationToken.company ?: throw CompanyNotFoundException("Company not found with token: $token")
    }

    override fun approveEmployee(token: String) {
        val verificationToken = verificationTokenService.getEmployeeVerificationToken(token)
        validateTokenExpiry(verificationToken.expiryDate)

        val employee = verificationToken.user ?: throw UserNotFoundException("User not found with token: $token")
        employee.isEmployeeVerified = true
        userService.save(employee)
        verificationTokenService.deleteEmployeeVerificationToken(verificationToken)

        messageProducer.publish(
            generateVerificationConfirmRequest(verificationToken.user!!.email, EmailType.EMPLOYEE_VERIFICATION_APPROVED),
            "internal.exchange",
            "internal.email.routing-key"
        )
    }

    override fun rejectEmployee(token: String) {
        val verificationToken = verificationTokenService.getEmployeeVerificationToken(token)
        verificationTokenService.deleteEmployeeVerificationToken(verificationToken)

        messageProducer.publish(
            generateVerificationConfirmRequest(verificationToken.user!!.email, EmailType.EMPLOYEE_VERIFICATION_REJECTED),
            "internal.exchange",
            "internal.email.routing-key"
        )
    }


    override fun verifyUserByToken(token: String) {
        val user = getUserFromToken(token)
        user.isEmailVerified = true
        userService.save(user)
        verificationTokenService.deleteVerificationToken(verificationTokenService.getVerificationToken(token))
    }

    override fun verifyCompanyByToken(token: String): Company {
        val company = getCompanyFromToken(token)
        company.isEmailVerified = true
        companyService.save(company)
        verificationTokenService.deleteVerificationToken(verificationTokenService.getVerificationToken(token))
        return company
    }

    override fun verifyByToken(token: String) {
        val verificationToken = verificationTokenService.getVerificationToken(token)
        if (verificationToken.user != null) {
            verifyUserByToken(token)
        } else if (verificationToken.company != null) {
            verifyCompanyByToken(token)
        } else {
            throw InvalidTokenException("Invalid token")
        }
    }

    private fun generateEmailRequest(to: String, token: String): EmailRequest {
        return EmailRequest(
            to = to,
            message = token,
            emailType = EmailType.EMAIL_VERIFICATION
        )
    }

    private fun generateEmployeeEmailRequest(to: String, token: String, employeeDetails: EmployeeDetails): EmailRequest {
        return EmailRequest(
            to = to,
            message = token,
            emailType = EmailType.EMPLOYEE_VERIFICATION,
            employeeDetails = employeeDetails
        )
    }

    private fun generateVerificationConfirmRequest(to: String, emailType: EmailType): EmailRequest {
        return EmailRequest(
            to = to,
            message = "",
            emailType = emailType
        )
    }

    private fun validateTokenExpiry(expiryDate: Date) {
        if (expiryDate.before(Date())) {
            throw InvalidTokenException("Token has expired")
        }
    }

    private fun getUserFromToken(token: String): User {
        val verificationToken = verificationTokenService.getVerificationToken(token)
        validateTokenExpiry(verificationToken.expiryDate)
        return verificationToken.user ?: throw UserNotFoundException("User not found with token: $token")
    }

    private fun generateEmployeeDetails(user: User): EmployeeDetails {
        return EmployeeDetails(
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email
        )
    }
}