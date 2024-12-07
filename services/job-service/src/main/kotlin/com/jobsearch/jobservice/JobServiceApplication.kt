package com.jobsearch.jobservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
//@EnableAspectJAutoProxy
class JobServiceApplication

fun main(args: Array<String>) {
    runApplication<JobServiceApplication>(*args)
}
