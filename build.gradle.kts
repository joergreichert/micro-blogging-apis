group = "de.l.joergreichert.twitter"
version = "1.0.0-SNAPSHOT"

plugins {
    id("org.springframework.boot")
    id("org.jetbrains.kotlin.plugin.spring")
    idea
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    implementation(platform(Spring.boms.dependencies))
    implementation("org.springframework.boot:spring-boot-starter:_")
    implementation(Spring.boot.web)
    implementation("org.springframework:spring-web:_")
    implementation("org.springframework.boot:spring-boot-starter-webflux:_")
    implementation("com.fasterxml.jackson.core:jackson-core:_")
    implementation("com.fasterxml.jackson.core:jackson-annotations:_")
    implementation("com.fasterxml.jackson.core:jackson-databind:_")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:_")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:_")
    implementation("org.mnode.ical4j:ical4j:_")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:_")
    implementation("io.netty:netty-resolver-dns-native-macos:4.1.114.Final:osx-aarch_64")

    testImplementation(Testing.junit.jupiter.api)
    testRuntimeOnly(Testing.junit.jupiter.engine)
    implementation(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
}