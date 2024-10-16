package com.jobsearch.messagequeue

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.stereotype.Component


@Component
class RabbitMQMessageProducer(
    private val amqpTemplate: AmqpTemplate
){
    private val logger = LoggerFactory.getLogger(RabbitMQMessageProducer::class.java)

    fun publish(payload: Any?, exchange: String?, routingKey: String?) {
        logger.info("publishing the message")
        amqpTemplate.convertAndSend(exchange, routingKey, payload)
        logger.info("published message")
    }
}