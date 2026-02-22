group = "de.l.joergreichert.twitter"
version = "1.0.0-SNAPSHOT"

plugins {
    id("org.springframework.boot")
    id("org.jetbrains.kotlin.plugin.spring")
    idea
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Kotlin.gradlePlugin)
    implementation(platform(Spring.boms.dependencies))
    implementation("org.springframework.boot:spring-boot-starter:_")
    implementation(Spring.boot.web)
    implementation("org.springframework:spring-web:_")
    implementation(Spring.boot.webflux)
    implementation("com.fasterxml.jackson.core:jackson-core:_")
    implementation("com.fasterxml.jackson.core:jackson-annotations:_")
    implementation("com.fasterxml.jackson.core:jackson-databind:_")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:_")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:_")
    implementation("org.mnode.ical4j:ical4j:_")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:_")
    implementation("io.netty:netty-resolver-dns-native-macos:_:osx-aarch_64")

    testImplementation(Testing.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:_")

    implementation(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    optimizedLaunch.set(false)
}

kotlin {
    jvmToolchain(25)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
        vendor = JvmVendorSpec.ADOPTIUM
    }
}
