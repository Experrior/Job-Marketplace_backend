package com.jobsearch.apigateway.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
@Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cors { cors ->
                cors.configurationSource { exchange ->
                    val configuration = CorsConfiguration().apply {
                        allowedOrigins = listOf("*")
                        allowedMethods = listOf("GET", "POST", "OPTIONS", "PUT", "DELETE")
                        allowedHeaders = listOf("*")
                    }
                    configuration
                }
            }
            .csrf { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges.anyExchange().permitAll()
            }
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route { r: PredicateSpec ->
                r.path("/user-service/**")
                    .filters { f: GatewayFilterSpec -> f.stripPrefix(1) }
                    .uri("http://172.22.0.1:8081")  //user service ip
                //todo change ip:port to env vars
            }
            .route { r: PredicateSpec ->
                r.path("/job-service/**")
                    .filters { f: GatewayFilterSpec -> f.stripPrefix(1) }
                    .uri("http://172.22.0.1:8083")  //user service ip
                //todo change ip:port to env vars
            }
            .route { r: PredicateSpec ->
                r.path("/chat-service/**")
                    .filters { f: GatewayFilterSpec -> f.stripPrefix(1) }
                    .uri("http://172.22.0.1:8088")  //user service ip
                    //todo change ip:port to env vars
            }
            .route { r: PredicateSpec ->
                r.path("/chat_service/**")
                    .filters { f: GatewayFilterSpec -> f.stripPrefix(1) }
                    .uri("ws://172.22.0.1:8088")
            }
            .build()
    }

}
