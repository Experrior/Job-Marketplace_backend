package com.jobsearch.jobservice.entities

import com.fasterxml.jackson.annotation.JsonCreator

data class Skill(
    val name: String,
    val level: Int
)