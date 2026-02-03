package de.l.joergreichert.outintheopen

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.l.joergreichert.outintheopen.config.AppProperties
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient
import java.time.format.DateTimeFormatter


@EnableConfigurationProperties(AppProperties::class)
@SpringBootApplication
class Application {
    internal val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun initializer(): CommandLineRunner =
        CommandLineRunner { args ->
            for (arg in args) {
                logger.info(arg)
            }
        }

    @PostConstruct
    @Throws(Exception::class)
    private fun init() {
        for (env in System.getenv().entries) {
            logger.info("ENV ${env.key}: ${env.value}")
        }
    }

    @Bean
    fun javaTimeModule(): JavaTimeModule {
        val module = JavaTimeModule()
        module.addSerializer(LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE))
        return module
    }

    @Bean
    fun objectMapper(javaTimeModule: JavaTimeModule): ObjectMapper {
        return jacksonObjectMapper().apply {
            this.registerModule(javaTimeModule);
        }
    }

    @Bean
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}