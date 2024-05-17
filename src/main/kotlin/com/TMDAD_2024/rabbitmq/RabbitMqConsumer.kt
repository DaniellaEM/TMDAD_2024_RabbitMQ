package com.TMDAD_2024.rabbitmq

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

data class MyMessage(val list: List<String>)

@Service
class RabbitMqConsumer(private val rabbitTemplate: RabbitTemplate) {

    companion object {
        private val trendMap = mutableMapOf<String, Int>()
        private val stopwords = mutableSetOf<String>()

        init {
            // Cargar stopwords desde el archivo txt
            val stopwordsFile = File("src/main/resources/stopwords.txt")
            if (stopwordsFile.exists()) {
                stopwordsFile.forEachLine { line ->
                    stopwords.add(line.trim().lowercase(Locale.getDefault()))
                }
            }
        }
    }

    @RabbitListener(queues = ["MESSAGE_QUEUE"])
    fun consume(message: String) {
        println("Received message -> $message")

        // Procesar el mensaje para extraer las palabras clave
        val words = processText(message)

        // Actualizar la lista de tendencias global
        synchronized(trendMap) {
            for (word in words) {
                trendMap[word] = trendMap.getOrDefault(word, 0) + 1
            }
        }

        // Obtener las tendencias actuales
        val currentTrends = synchronized(trendMap) {
            trendMap.toList().sortedByDescending { it.second }
        }

        // Imprimir la lista de tendencias
        println("Tendencias actuales:")
        currentTrends.forEach { (word, count) ->
            println("$word: $count")
        }

        // Formar un mensaje único con las palabras clave
        val trendingTopicsMessage = currentTrends.joinToString(separator = ", ")


        // Send the received message to the new queue
        rabbitTemplate.convertAndSend("SECOND_MESSAGE_QUEUE", trendingTopicsMessage)
    }

    private fun processText(text: String): List<String> {
        // Filtrar palabras permitidas y eliminar signos de puntuación
        val filteredWords = text.split(Regex("\\s+"))
                .map { it.replace(Regex("[,.?!]"), "").lowercase(Locale.getDefault()) }
                .filter { it.matches(Regex("[a-zA-ZáéíóúüÁÉÍÓÚÜñÑ]+")) && !stopwords.contains(it) }

        // Contar la frecuencia de cada palabra
        val wordCounts = filteredWords.groupingBy { it }.eachCount()

        return wordCounts.toList().sortedByDescending { it.second }.map { it.first }
    }
}
@Service
class SecondQueueConsumer {

    @RabbitListener(queues = ["SECOND_MESSAGE_QUEUE"])
    fun consume(message: String) {
        println("Received message from SECOND_MESSAGE_QUEUE -> $message")
    }
}