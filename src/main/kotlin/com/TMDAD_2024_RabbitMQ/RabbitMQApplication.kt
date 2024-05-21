package com.TMDAD_2024_RabbitMQ


import com.TMDAD_2024_RabbitMQ.rabbitmq.RabbitMqConsumer
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class TrendsSender : CommandLineRunner {
	override fun run(vararg args: String?) {
		val scheduler = Executors.newSingleThreadScheduledExecutor()
		val intervalInSeconds = 5L

		scheduler.scheduleAtFixedRate({
			RabbitMqConsumer.sendToRabbit()
		}, 0, intervalInSeconds, TimeUnit.SECONDS)
	}
}

@SpringBootApplication
class RabbitMQApplication

fun main(args: Array<String>) {
	runApplication<RabbitMQApplication>(*args)
}
