package com.TMDAD_2024_RabbitMQ.rabbitmq

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RabbitMqProducer @Autowired constructor(private val rabbitTemplate: RabbitTemplate) {

    fun sendMessage(message: String) {
        println("Message sent -> $message")
        rabbitTemplate.convertAndSend("MESSAGE_EXCHANGE", "MESSAGE_ROUTING_KEY", message)
    }
}