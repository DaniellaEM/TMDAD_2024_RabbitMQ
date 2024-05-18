package com.TMDAD_2024_RabbitMQ.rabbitmq

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class RabbitMqController(private val producer: RabbitMqProducer) {

    @GetMapping("/publish")
    fun sendMessage(@RequestParam("message") message: String): ResponseEntity<String> {
        producer.sendMessage(message)
        return ResponseEntity.ok("Message sent to RabbitMQ ...")
    }
}