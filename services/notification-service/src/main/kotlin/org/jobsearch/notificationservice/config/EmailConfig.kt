package org.jobsearch.notificationservice.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class EmailConfig {
    @Value("\${rabbitmq.exchanges.internal}")
    val internalExchange: String? = null

    @Value("\${rabbitmq.queue.email}")
    val notificationQueue: String? = null

    @Value("\${rabbitmq.routing-keys.internal-email}")
    val internalNotificationRoutingKey: String? = null

    @Bean
    fun internalTopicExchange(): TopicExchange {
        return TopicExchange(this.internalExchange)
    }

    @Bean
    fun notificationQueue(): Queue {
        return Queue(this.notificationQueue)
    }

    @Bean
    fun internalToNotificationBinding(): Binding {
        return BindingBuilder
            .bind(notificationQueue())
            .to(internalTopicExchange())
            .with(this.internalNotificationRoutingKey)
    }
}