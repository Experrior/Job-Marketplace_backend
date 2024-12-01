package com.jobsearch.apigateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration


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


//    @Bean
//    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
//        return builder.routes()
//            .route { r: PredicateSpec ->
//                r.path("/user-service/**")
//                    .filters { f: GatewayFilterSpec -> f.stripPrefix(1) }
//                    .uri("http://172.22.0.1:8081")  //user service ip
//                    //todo change ip:port to env vars
//            }
//            .build()
//    }

//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource {
//        val configuration = CorsConfiguration()
//        configuration.allowedOrigins = listOf("*")
//        configuration.allowedMethods = listOf("GET", "POST", "OPTIONS", "PUT", "DELETE")
//        configuration.allowedHeaders = listOf("*")
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", configuration)
//        return source
//    }


}
