package com.TMDAD_2024_RabbitMQ.rabbitmq

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.io.File
import java.util.*

data class MyMessage(val list: List<String>)

@EnableScheduling
@Service
class RabbitMqConsumer(/*@Autowired private val rabbitTemplate: RabbitTemplate*/) {

    companion object {
        private val trendMap = mutableMapOf<String, Int>()
        private val stopwords = mutableSetOf<String>()

        @Autowired
        private lateinit var rabbitTemplate: RabbitTemplate

        init {
            // Cargar stopwords desde el archivo txt
            val stopwordsFile = File("src/main/resources/stopwords.txt")
            if (stopwordsFile.exists()) {
                stopwordsFile.forEachLine { line ->
                    stopwords.add(line.trim().lowercase(Locale.getDefault()))
                }
            }
        }

        @Synchronized
        fun add(words: List<String>)
        {
            // Actualizar la lista de tendencias global
            for (word in words) {
                trendMap[word] = trendMap.getOrDefault(word, 0) + 1
            }
        }

        @Synchronized
        fun sendToRabbit() {
            if(trendMap.isEmpty())
            {
                println("Todavia sin datos")
                rabbitTemplate.convertAndSend("SECOND_MESSAGE_QUEUE", "Todavia sin datos, envia mensajes!")
                return
            }

            // Obtener las tendencias actuales
            val currentTrends = trendMap.toList().sortedByDescending { it.second }

            // Imprimir la lista de tendencias
            println("Tendencias actuales:")
            currentTrends.forEach { (word, count) ->
                println("$word: $count")
            }

            // Formar un mensaje único con las palabras clave ordenado
            val trendingTopicsMessage = currentTrends.mapIndexed { index, pair ->
                "${index + 1}.º ${pair.first}: ${pair.second}"
            }.joinToString(separator = ", ")

            // Enviar los trending topics a la nueva cola
            rabbitTemplate.convertAndSend("SECOND_MESSAGE_QUEUE", trendingTopicsMessage)
        }

        @Synchronized
        fun reset()
        {
            trendMap.clear()
        }
    }

    @RabbitListener(queues = ["MESSAGE_QUEUE"])
    fun consume(message: String) {
        println("Received message -> $message")

        // Procesar el mensaje para extraer las palabras clave
        val words = processText(message)

//        // Actualizar la lista de tendencias global
//        synchronized(trendMap) {
//            for (word in words) {
//                trendMap[word] = trendMap.getOrDefault(word, 0) + 1
//            }
//        }
        add(words)

//        // Obtener las tendencias actuales
//        val currentTrends = synchronized(trendMap) {
//            trendMap.toList().sortedByDescending { it.second }
//        }
//
//        // Imprimir la lista de tendencias
//        println("Tendencias actuales:")
//        currentTrends.forEach { (word, count) ->
//            println("$word: $count")
//        }
//
//        // Formar un mensaje único con las palabras clave ordenado
//        val trendingTopicsMessage = currentTrends.mapIndexed { index, pair ->
//            "${index + 1}.º ${pair.first}: ${pair.second}"
//        }.joinToString(separator = ", ")
//
//
//        // Enviar los trending topics a la nueva cola
//        rabbitTemplate.convertAndSend("SECOND_MESSAGE_QUEUE", trendingTopicsMessage)
        sendToRabbit()
    }

    fun send(m: String)
    {

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

    // Resetear a la lista de trending topics todos los dias a la media noche
    @Scheduled(cron = "0 0 0 * * ?")
    fun resetTrends() {
        println("Resetting trending topics")
        reset()
    }
}