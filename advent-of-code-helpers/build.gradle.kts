import org.gradle.kotlin.dsl.invoke

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
    implementation(libs.kotlinx.coroutines.core.jvm)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}