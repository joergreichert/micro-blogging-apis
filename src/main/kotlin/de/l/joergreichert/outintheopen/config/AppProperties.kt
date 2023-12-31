package de.l.joergreichert.outintheopen.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val twitter: TwitterConfig,
    val mastodon: MastodonConfig,
    val bluesky: BlueSkyConfig
)

data class TwitterConfig(
    val accountId: String,
    val clientId: String,
    val clientSecret: String,
    val consumerKey: String,
    val consumerSecret: String,
)

data class MastodonConfig(
    val accountId: String,
    val website: String,
    val clientId: String,
    val clientSecret: String,
    val consumerKey: String,
    val consumerSecret: String,
)

data class BlueSkyConfig(
    val accountId: String,
)