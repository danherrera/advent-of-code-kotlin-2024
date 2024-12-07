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
    implementation(libs.kotlinx.coroutines.core.jvm)
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

tasks.register<JavaExec>("run") {
    dependsOn("build")
    val dayArg = project.findProperty("day")?.toString()?.padStart(2, '0')
        ?: error("Please provide which day to run. Example: ./gradlew run -Pday=1")
    mainClass.set("Day${dayArg}Kt")
    classpath = sourceSets["main"].runtimeClasspath
}