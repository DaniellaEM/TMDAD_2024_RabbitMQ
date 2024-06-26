package com.TMDAD_2024_RabbitMQ.rabbitmq

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.SimpleMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitMqConfig {
    @Bean
    fun queue(): Queue {
        return Queue("MESSAGE_QUEUE")
    }

    @Bean
    fun newQueue(): Queue {
        return Queue("TRENDING_QUEUE")
    }

    @Bean
    fun converter(): SimpleMessageConverter {
        val converter: SimpleMessageConverter = SimpleMessageConverter()
        converter.setAllowedListPatterns(listOf("java.util.*"))
        return converter
    }

}