plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.flyway)
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "server"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

// ===========================
// INTEGRATION TEST CONFIG
// ===========================

val integrationTest by sourceSets.creating {
    kotlin.srcDir("src/integrationTest/kotlin")
    resources.srcDir("src/integrationTest/resources")

    compileClasspath += sourceSets["main"].output +
        configurations["testRuntimeClasspath"]

    runtimeClasspath += output + compileClasspath
}

configurations[integrationTest.implementationConfigurationName]
    .extendsFrom(configurations.testImplementation.get())

configurations[integrationTest.runtimeOnlyConfigurationName]
    .extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    // ===== MAIN =====
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(libs.springdoc.openapi)

    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)
    runtimeOnly(libs.postgresql)

    implementation(libs.flyway.core)

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // ===== UNIT TEST =====
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use Kotest bundle from version catalog
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.mockk)

    testImplementation(libs.archunit)
    testImplementation(libs.jackson.module.kotlin)
    testImplementation(libs.h2)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // ===== INTEGRATION TEST =====
    add("integrationTestImplementation", platform(libs.testcontainers.bom))
    add("integrationTestImplementation", libs.bundles.testcontainers)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xannotation-default-target=param-property",
        )
    }
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}

flyway {
    url = (project.findProperty("spring.datasource.url") as String?) ?: "jdbc:h2:mem:testdb"
    user = (project.findProperty("spring.datasource.username") as String?) ?: "sa"
    password = (project.findProperty("spring.datasource.password") as String?) ?: ""
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests"
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    shouldRunAfter(tasks.test)

    useJUnitPlatform()

    // Attiva automaticamente profilo container
    systemProperty("spring.profiles.active", "container")
}

tasks.check {
    dependsOn("integrationTest")
}

tasks.named<Copy>("processIntegrationTestResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
