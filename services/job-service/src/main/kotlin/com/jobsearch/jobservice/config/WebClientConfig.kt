package com.jobsearch.jobservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Configuration
class WebClientConfig {
    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader("Content-Type", "application/json")
            .filter { request, next ->
                next.exchange(request)
                    .timeout(Duration.ofSeconds(5))
                    .doOnError { e -> println("Error: ${e.message}") }
            }
            .build()
    }
}