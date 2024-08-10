package de.l.joergreichert.outintheopen

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.l.joergreichert.outintheopen.bluesky.to.Feeds
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.time.format.DateTimeFormatter

class ParseTest {

    @Test
    @Disabled
    fun testParseFeeds() {
        javaClass.classLoader.getResourceAsStream("feeds.json").use {
            input ->
            val objectMapper = jacksonObjectMapper().apply {
                this.registerModule(
                    JavaTimeModule().apply {
                        this.addSerializer(LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE))
                    }
                )
            }

            val content = input!!.bufferedReader().use(BufferedReader::readText)
            println(objectMapper.readValue(content, Feeds::class.java))
        }
    }
}