package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.exceptions.CompanyNotFoundException
import com.jobsearch.userservice.repositories.CompanyRepository
import com.jobsearch.userservice.replica_repositories.CompanyRepositoryReplica
import org.springframework.stereotype.Service
import java.util.*

@Service
class CompanyServiceImpl(
    private val companyRepository: CompanyRepository,
    private val companyRepositoryReplica: CompanyRepositoryReplica,
    private val fileStorageService: FileStorageService
): CompanyService {
    override fun existsByEmail(email: String): Boolean {
        return companyRepositoryReplica.existsByEmail(email)
    }

    override fun existsByName(name: String): Boolean {
        return companyRepositoryReplica.existsByName(name)
    }

    override fun save(company: Company): Company {
        return companyRepository.save(company)
    }

    override fun findCompanyIdByName(name: String): UUID {
        return companyRepositoryReplica.findCompanyByName(name).companyId
            ?: throw CompanyNotFoundException("Company not found with name: $name")
    }

    override fun findCompanyById(companyId: UUID): Company {
        val company = companyRepositoryReplica.findCompanyByCompanyId(companyId)
            ?: throw CompanyNotFoundException("Company not found with id: $companyId")
        company.logoUrl = company.s3LogoPath?.let { fileStorageService.getFileUrl(it) }
        return company
    }

    override fun getAllCompanies(): List<Company> {
        val companies = companyRepositoryReplica.findAll()
        companies.forEach { company ->
            company.logoUrl = company.s3LogoPath?.let { fileStorageService.getFileUrl(it) }
        }

        return companies
    }
}