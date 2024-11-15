package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.exceptions.CompanyNotFoundException
import com.jobsearch.userservice.repositories.CompanyRepository
import org.springframework.stereotype.Service
import java.util.*

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

    override fun getCompanyIdByName(name: String): UUID {
        return companyRepository.findCompanyByName(name).companyId
            ?: throw CompanyNotFoundException("Company not found with name: $name")
    }

    override fun findCompanyById(companyId: UUID): Company {
        return companyRepository.findCompanyByCompanyId(companyId)
            ?: throw CompanyNotFoundException("Company not found with id: $companyId")
    }
}