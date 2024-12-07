package com.jobsearch.jobservice.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
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

    @Value("\${aws.region}")
    private lateinit var region: String

    @Bean
    fun s3Client(): S3Client {
        val credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        )

        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        )

        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build()
    }
}
