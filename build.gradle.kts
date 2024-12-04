import org.jetbrains.kotlin.gradle.internal.config.LanguageFeature

plugins {
    kotlin("jvm") version "2.1.0"
}

group = "dev.danherrera"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":advent-of-code-helpers"))
    testImplementation(kotlin("test"))
}

kotlin {
    sourceSets.all {
        languageSettings {
            enableLanguageFeature(LanguageFeature.ContextReceivers.name)
        }
    }
}

tasks.test {
    useJUnitPlatform()
}