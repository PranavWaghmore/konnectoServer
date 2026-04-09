
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "pw.coding"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.websockets)
    implementation("io.ktor:ktor-server-sessions")
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")

    // Gson
    testImplementation("com.google.code.gson:gson:2.8.9")

    // KMongo
    implementation("org.litote.kmongo:kmongo:5.2.0")
    implementation("org.litote.kmongo:kmongo-coroutine:5.2.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")


    // Koin core features
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("io.insert-koin:koin-ktor:3.5.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.0")

    // Test dependencies

    // Koin
    testImplementation("io.insert-koin:koin-test:3.5.0")
    // Kotlin Test
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.1.10")
    // Truth
    testImplementation("com.google.truth:truth:1.4.4")

    implementation("org.fusesource.jansi:jansi:2.4.1")

}
