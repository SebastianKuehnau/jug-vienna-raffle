package com.vaadin.demo.application.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.vaadin.demo.application", importOptions = {ImportOption.DoNotIncludeTests.class})
public class HexagonalArchitectureTest {

    // Define package constants
    private static final String DOMAIN_PACKAGE = "com.vaadin.demo.application.domain..";
    private static final String DOMAIN_MODEL_PACKAGE = "com.vaadin.demo.application.domain.model..";
    private static final String DOMAIN_PORT_PACKAGE = "com.vaadin.demo.application.domain.port..";
    private static final String APPLICATION_PACKAGE = "com.vaadin.demo.application.application..";
    private static final String ADAPTER_PACKAGE = "com.vaadin.demo.application.adapter..";
    private static final String REPOSITORY_PACKAGE = "com.vaadin.demo.application.repository..";
    private static final String DATA_PACKAGE = "com.vaadin.demo.application.data..";
    private static final String VIEW_PACKAGE = "com.vaadin.demo.application.views..";
    
    // Define layer names for layered architecture
    private static final String DOMAIN_LAYER = "Domain";
    private static final String APPLICATION_LAYER = "Application";
    private static final String ADAPTER_LAYER = "Adapter";
    private static final String INFRASTRUCTURE_LAYER = "Infrastructure";
    private static final String UI_LAYER = "UI";

    /**
     * This test verifies that domain ports are interfaces.
     * This is a key principle of hexagonal architecture.
     */
    @Test
    @DisplayName("Domain Ports Should Be Interfaces")
    public void domainPortsShouldBeInterfaces() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.vaadin.demo.application.domain.port");

        ArchRule rule = classes()
                .that().resideInAPackage(DOMAIN_PORT_PACKAGE)
                .should().beInterfaces();

        rule.check(importedClasses);
    }

    /**
     * This test verifies that domain models with "Record" suffix are records.
     * Using records for domain models ensures immutability,
     * which is a good practice for domain objects.
     */
    @Test
    @DisplayName("Domain Models with 'Record' Suffix Should Be Records")
    public void domainModelsWithRecordSuffixShouldBeRecords() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.vaadin.demo.application.domain.model");

        ArchRule rule = classes()
                .that().resideInAPackage(DOMAIN_MODEL_PACKAGE)
                .and().haveNameMatching(".*Record")
                .should().beRecords();

        rule.check(importedClasses);
    }
    
    /**
     * This test verifies that application services depend on domain ports or models.
     * This is a core principle of hexagonal architecture - the application layer
     * should depend on domain abstractions.
     */
    @Test
    @DisplayName("Application Services Should Use Domain")
    public void applicationServicesShouldUseDomain() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.vaadin.demo.application");

        ArchRule rule = classes()
                .that().resideInAPackage("com.vaadin.demo.application.application.service..")
                .and().haveNameMatching(".*ApplicationService")
                .should().dependOnClassesThat().resideInAnyPackage(
                        DOMAIN_PORT_PACKAGE, 
                        DOMAIN_MODEL_PACKAGE
                );

        rule.check(importedClasses);
    }
    
    /**
     * This test verifies that domain models don't depend on Spring infrastructure.
     * Domain models should be pure business logic, not tied to any framework.
     */
    @Test
    @DisplayName("Domain Models Should Not Depend On Spring")
    public void domainModelsShouldNotDependOnSpring() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.vaadin.demo.application");

        ArchRule rule = noClasses()
                .that().resideInAPackage(DOMAIN_MODEL_PACKAGE)
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "javax.persistence..",
                        "jakarta.persistence.."
                );

        rule.check(importedClasses);
    }
    
    /**
     * This test verifies that UI components don't directly use JPA entities.
     * They should only use domain records to maintain proper separation.
     */
    @Test
    @DisplayName("UI Should Not Depend On Data Entities")
    public void uiShouldNotDependOnDataEntities() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.vaadin.demo.application");

        ArchRule rule = noClasses()
                .that().resideInAPackage(VIEW_PACKAGE)
                .should().dependOnClassesThat().resideInAPackage(DATA_PACKAGE);

        rule.check(importedClasses);
    }
    
    /**
     * This test verifies the overall layered architecture of the application.
     * It defines the allowed dependencies between layers according to hexagonal architecture.
     */
    @Test
    @DisplayName("Layered Architecture Should Be Respected")
    public void layeredArchitectureShouldBeRespected() {
        JavaClasses importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.vaadin.demo.application");

        ArchRule rule = layeredArchitecture()
                .consideringAllDependencies()
                .layer(DOMAIN_LAYER).definedBy(DOMAIN_PACKAGE)
                .layer(APPLICATION_LAYER).definedBy(APPLICATION_PACKAGE)
                .layer(ADAPTER_LAYER).definedBy(ADAPTER_PACKAGE)
                .layer(INFRASTRUCTURE_LAYER).definedBy(REPOSITORY_PACKAGE, DATA_PACKAGE)
                .layer(UI_LAYER).definedBy(VIEW_PACKAGE)
                
                // Define allowed dependencies
                .whereLayer(DOMAIN_LAYER).mayOnlyBeAccessedByLayers(APPLICATION_LAYER, ADAPTER_LAYER, UI_LAYER)
                .whereLayer(APPLICATION_LAYER).mayOnlyBeAccessedByLayers(ADAPTER_LAYER, UI_LAYER)
                .whereLayer(ADAPTER_LAYER).mayOnlyBeAccessedByLayers(UI_LAYER)
                .whereLayer(INFRASTRUCTURE_LAYER).mayOnlyBeAccessedByLayers(ADAPTER_LAYER);
                
        rule.check(importedClasses);
    }
}