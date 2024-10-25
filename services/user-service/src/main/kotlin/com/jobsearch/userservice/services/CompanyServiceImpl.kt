package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.repositories.CompanyRepository
import org.springframework.stereotype.Service

@Service
class CompanyServiceImpl(
    private val companyRepository: CompanyRepository
): CompanyService {
    override fun existsByEmail(email: String): Boolean {
        return companyRepository.existsByEmail(email)
    }

    override fun existsByName(name: String): Boolean {
        return companyRepository.existsByName(name)
    }

    override fun save(company: Company): Company {
        return companyRepository.save(company)
    }
}