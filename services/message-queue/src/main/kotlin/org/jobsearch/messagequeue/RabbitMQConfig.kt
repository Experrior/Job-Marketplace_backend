package org.jobsearch.messagequeue

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitMQConfig(
    private val connectionFactory: ConnectionFactory
) {
    @Bean
    fun amqpTemplate(): AmqpTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = jacksonConverter()
        return rabbitTemplate
    }

    @Bean
    fun simpleRabbitListenerContainerFactory(): SimpleRabbitListenerContainerFactory {
        val factory =
            SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(connectionFactory)
        factory.setMessageConverter(jacksonConverter())
        return factory
    }

    @Bean
    fun jacksonConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }
}