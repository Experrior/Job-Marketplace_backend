package com.jobsearch.userservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class S3Config {

    @Value("\${aws.access.key}")
    private lateinit var accessKey: String

    @Value("\${aws.secret.key}")
    private lateinit var secretKey: String

    @Value("\${aws.session.token}")
    private var sessionToken: String? = null

    @Value("\${aws.region}")
    private lateinit var region: String

    @Bean
    fun s3Client(): S3Client {
        val credentialsProvider = StaticCredentialsProvider.create(
            AwsSessionCredentials.create(accessKey, secretKey, sessionToken)
        )

        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(AwsSessionCredentials.create(accessKey, secretKey, sessionToken)))
            .build()
    }
}
