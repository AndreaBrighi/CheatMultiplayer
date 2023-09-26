plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.taskTree)
    alias(libs.plugins.dokka)
    alias(libs.plugins.multiJvmTesting)
}

allprojects {
    group = "it.unibo.ds"
    version = "0.1.0"
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "org.danilopianini.multi-jvm-test-plugin")

    tasks.test {
        useJUnitPlatform()
    }

    multiJvm {
        maximumSupportedJvmVersion.set(latestJavaSupportedByGradle)
    }
}
