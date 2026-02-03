rootProject.buildFileName = "build.gradle.kts"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.gradle.toolchains.foojay-resolver-convention") {
                useModule(
                    "org.gradle.toolchains.foojay-resolver-convention:" +
                            "org.gradle.toolchains.foojay-resolver-convention.gradle.plugin:" +
                            "${requested.version}"
                )
            }
        }
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.6"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

