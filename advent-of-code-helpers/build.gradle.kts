import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.sourceSets
import org.jetbrains.kotlin.gradle.internal.config.LanguageFeature

plugins {
    kotlin("jvm")
}

group = "dev.danherrera"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    sourceSets.all {
        languageSettings {
        }
    }
}

dependencies {
    implementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}