package org.example.server.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import kotlin.test.Test

class CleanArchitectureTest {

    @Test
    fun layerDependencies() {
        val importedClasses = ClassFileImporter().importPackages("org.example.server")
        val cleanArchitecture = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy("..domainLayer..")
            .layer("Business").definedBy("..businessLayer..")
            .layer("Adapter").definedBy("..interfaceAdaptersLayer..")
            .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()
            .whereLayer("Business").mayOnlyBeAccessedByLayers("Adapter")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Business", "Adapter")
        cleanArchitecture.check(importedClasses)
    }

    @Test
    fun everythingPassThroughBusinessLayer() {
        val importedClasses = ClassFileImporter().importPackages("org.example.server")
        val ruleEverythingPassThroughBusinessLayer = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Business").definedBy("..businessLayer..")
            .layer("Controller").definedBy("..interfaceAdaptersLayer.controllers..")
            .layer("Persistence").definedBy("..interfaceAdaptersLayer.persistence..")
            .layer("Security").definedBy("..interfaceAdaptersLayer.security..")
            .whereLayer("Persistence").mayNotBeAccessedByAnyLayer()
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Security").mayNotBeAccessedByAnyLayer()
            .whereLayer("Business").mayOnlyBeAccessedByLayers("Controller", "Persistence", "Security")
        ruleEverythingPassThroughBusinessLayer.check(importedClasses)
    }
}
