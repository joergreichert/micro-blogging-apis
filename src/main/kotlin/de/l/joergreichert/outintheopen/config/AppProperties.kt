package de.l.joergreichert.outintheopen.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "app")
@ConstructorBinding
data class AppProperties(
    val twitter: TwitterConfig
)

@ConstructorBinding
data class TwitterConfig(
    val clientId: String? = null,
    val clientSecret: String? = null,
)