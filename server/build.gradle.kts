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

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(libs.springdoc.openapi) {
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }
    // commons-lang3 managed via dependencyManagement below (pin to a safe version there)
    implementation(libs.jwt)
    runtimeOnly(libs.postgresql)
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.archunit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.jackson.module.kotlin)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.h2) // Add H2 for in-memory database during tests

    // Testcontainers for integration tests with real PostgreSQL
    testImplementation(libs.bundles.testcontainers)

    // Flyway runtime
    implementation(libs.flyway.core)
}

dependencyManagement {
    imports {
        mavenBom(
            libs.testcontainers.bom
                .get()
                .toString(),
        )
    }
    dependencies {
        // Force a safe commons-lang3 across the project to avoid transitive vulnerable versions
        dependency(
            libs.apache.commons.lang3
                .get()
                .toString(),
        )
    }
}

flyway {
    url = (project.findProperty("spring.datasource.url") as String?) ?: "jdbc:h2:mem:testdb"
    user = (project.findProperty("spring.datasource.username") as String?) ?: "sa"
    password = (project.findProperty("spring.datasource.password") as String?) ?: ""
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
