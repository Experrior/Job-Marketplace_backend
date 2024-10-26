package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Company
import java.util.*

interface CompanyService {
    fun existsByEmail(email: String): Boolean
    fun existsByName(name: String): Boolean
    fun save(company: Company): Company
    fun getCompanyIdByName(name: String): UUID
}