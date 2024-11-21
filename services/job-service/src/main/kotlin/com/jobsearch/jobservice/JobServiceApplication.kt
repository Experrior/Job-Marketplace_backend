package com.jobsearch.jobservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JobServiceApplication

fun main(args: Array<String>) {
    runApplication<JobServiceApplication>(*args)
}
