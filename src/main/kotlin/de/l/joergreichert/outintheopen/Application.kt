package de.l.joergreichert.outintheopen

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import de.l.joergreichert.outintheopen.config.AppProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct


@EnableConfigurationProperties(AppProperties::class)
@SpringBootApplication
class Application {
    internal val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun initializer(): CommandLineRunner =
        CommandLineRunner { args: Array<String?> ->
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
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}