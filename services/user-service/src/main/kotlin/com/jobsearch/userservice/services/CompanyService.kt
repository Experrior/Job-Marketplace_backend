package com.jobsearch.userservice.services

import com.jobsearch.userservice.entities.Company

interface CompanyService {
    fun existsByEmail(email: String): Boolean
    fun existsByName(name: String): Boolean
    fun save(company: Company): Company
}