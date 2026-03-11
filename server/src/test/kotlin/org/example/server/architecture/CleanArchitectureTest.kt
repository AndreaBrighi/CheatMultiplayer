package org.example.server.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import kotlin.test.Test

class CleanArchitectureTest {
    @Test
    fun cleanArchitectureUncleBobStyle() {
        val importedClasses =
            ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("org.example.server")

        layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain")
            .definedBy("..domain..")
            .layer("Application")
            .definedBy("..application..")
            .layer("Adapters")
            .definedBy("..adapters..")
            .layer("Infrastructure")
            .definedBy("..infrastructure..")
            // Domain
            .whereLayer("Domain")
            .mayNotAccessAnyLayer()
            // Application
            .whereLayer("Application")
            .mayOnlyAccessLayers("Domain")
            // Adapters
            .whereLayer("Adapters")
            .mayOnlyAccessLayers("Application", "Domain")
            // Infrastructure
            .whereLayer("Infrastructure")
            .mayOnlyAccessLayers("Adapters", "Application", "Domain")
            .check(importedClasses)
    }
}
