plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.2")
    implementation("com.fasterxml.jackson:jackson-bom:2.15.2")
    implementation("com.alphacephei:vosk:0.3.45")
    implementation("com.aallam.openai:openai-client:3.2.0")
    implementation("io.ktor:ktor-client-okhttp:2.2.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("net.andrewcpu:elevenlabs-api:2.1")
    implementation("com.googlecode.soundlibs:jlayer:1.0.1.4")
    implementation("com.ibm.icu:icu4j:73.2")
    implementation("org.ocpsoft.prettytime:prettytime:5.0.1.Final")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}