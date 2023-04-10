import org.gradle.internal.impldep.bsh.commands.dir

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.blossom"
version = "1"

repositories {
    mavenCentral()
    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation("com.alphacephei:vosk:0.3.45")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("com.aallam.openai:openai-client:3.2.0")
    implementation("io.ktor:ktor-client-okhttp:2.2.4")
    implementation("com.tinder.statemachine:statemachine:0.2.0")
    implementation("org.yaml:snakeyaml:2.0")
    implementation("com.google.cloud:google-cloud-texttospeech:2.15.0")
    implementation(files("/lib/elevenlabs-api-1.1-SNAPSHOT-full.jar"))
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}