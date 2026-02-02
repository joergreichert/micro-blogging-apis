rootProject.buildFileName = "build.gradle.kts"

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.6"
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")