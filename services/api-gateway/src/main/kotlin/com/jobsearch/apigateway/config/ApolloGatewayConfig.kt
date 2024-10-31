package com.jobsearch.apigateway.config

import com.apollographql.federation.graphqljava.Federation
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class ApolloGatewayConfig {
    private val webClient: WebClient = WebClient.create()

    @Bean
    fun graphQL(): GraphQL {
        // Fetch schemas asynchronously
        val userServiceSchema = fetchSchema("http://user-service/graphql/schema").block()
        val jobServiceSchema = fetchSchema("http://job-service/graphql/schema").block()

        // Merge schemas
        val typeRegistry = TypeDefinitionRegistry()
        typeRegistry.merge(SchemaParser().parse(userServiceSchema))
        typeRegistry.merge(SchemaParser().parse(jobServiceSchema))

        val runtimeWiring = RuntimeWiring.newRuntimeWiring().build()
        val schema = Federation.transform(
            SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring)
        ).build()

        return GraphQL.newGraphQL(schema).build()
    }

    private fun fetchSchema(url: String): Mono<String> {
        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String::class.java)
            .onErrorResume { Mono.error(RuntimeException("Failed to fetch schema from $url")) }
    }
}