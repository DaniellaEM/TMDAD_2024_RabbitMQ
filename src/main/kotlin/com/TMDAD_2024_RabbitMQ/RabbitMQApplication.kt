package com.TMDAD_2024_RabbitMQ

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

//@Component
//class ConsoleMessagePrinter : CommandLineRunner {
//
//	override fun run(vararg args: String?) {
//		val scheduler = Executors.newSingleThreadScheduledExecutor()
//		val intervalInSeconds = 30L
//
//		scheduler.scheduleAtFixedRate({
//			println("Printing a message every $intervalInSeconds seconds")
//		}, 0, intervalInSeconds, TimeUnit.SECONDS)
//	}
//}

@SpringBootApplication
class RabbitMQApplication

fun main(args: Array<String>) {
	runApplication<RabbitMQApplication>(*args)
}
