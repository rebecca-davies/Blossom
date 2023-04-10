plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.blossom"
version = "1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.alphacephei:vosk:0.3.45")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("com.aallam.openai:openai-client:3.2.0")
    implementation("io.ktor:ktor-client-okhttp:2.2.4")
    implementation("com.tinder.statemachine:statemachine:0.2.0")
    implementation("org.yaml:snakeyaml:2.0")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}