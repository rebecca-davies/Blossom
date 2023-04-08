plugins {
    kotlin("jvm") version "1.8.0"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "org.blossom"
version = "1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.alphacephei:vosk:0.3.45")
    implementation("com.orctom:vad4j:1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("no.tornado:tornadofx:1.7.20")
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}