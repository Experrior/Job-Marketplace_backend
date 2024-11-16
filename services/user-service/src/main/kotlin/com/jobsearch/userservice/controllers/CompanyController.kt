package com.jobsearch.userservice.controllers

import com.jobsearch.userservice.entities.Company
import com.jobsearch.userservice.services.CompanyService
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class CompanyController(
    private val companyService: CompanyService
) {
    @QueryMapping
    fun companies(): List<Company> {
        return companyService.getAllCompanies()
    }
}