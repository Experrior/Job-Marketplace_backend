package com.jobsearch.messagequeue

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.stereotype.Component


@Component
class RabbitMQMessageProducer(
    private val amqpTemplate: AmqpTemplate
){

    fun publish(payload: Any?, exchange: String?, routingKey: String?) {
        amqpTemplate.convertAndSend(exchange, routingKey, payload)

    }
}