package com.TMDAD_2024.rabbitmq

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import org.springframework.messaging.simp.SimpMessagingTemplate

@Service
class RabbitMqConsumer(private val messagingTemplate: SimpMessagingTemplate, private val rabbitTemplate: RabbitTemplate) {

    private val trendMap = mutableMapOf<String, Int>()
    @RabbitListener(queues = ["MESSAGE_QUEUE"])
    fun consume(message: String) {
        println("Received message -> $message")

        // Analizar el mensaje para extraer las palabras clave
        val words = message.split("\\s+")

        // Actualizar la lista de tendencias
        for (word in words) {
            trendMap[word] = trendMap.getOrDefault(word, 0) + 1
        }

        // Imprimir la lista de tendencias
        println("Tendencias actuales:")
        trendMap.forEach { (word, count) ->
            println("$word: $count")
        }

        // Send the received message down the WebSocket
        messagingTemplate.convertAndSend("/topic/trendings", message)

        // Send the received message to the new queue
        rabbitTemplate.convertAndSend("SECOND_MESSAGE_QUEUE", message)
    }
}