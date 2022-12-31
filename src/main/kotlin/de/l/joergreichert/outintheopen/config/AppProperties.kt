package de.l.joergreichert.outintheopen.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "app")
@ConstructorBinding
data class AppProperties(
    val twitter: TwitterConfig,
    val mastodon: MastodonConfig
)

@ConstructorBinding
data class TwitterConfig(
    val accountId: String,
    val clientId: String,
    val clientSecret: String,
    val consumerKey: String,
    val consumerSecret: String,
)

@ConstructorBinding
data class MastodonConfig(
    val accountId: String,
    val website: String,
    val clientId: String,
    val clientSecret: String,
    val consumerKey: String,
    val consumerSecret: String,
)