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
class HexagonalArchitectureTest {

    // Package constants
    private static final String BASE_PACKAGE = "com.vaadin.demo.application";

    private static final String DOMAIN_PACKAGE = BASE_PACKAGE + ".domain..";
    private static final String DOMAIN_MODEL_PACKAGE = BASE_PACKAGE + ".domain.model..";

    private static final String APPLICATION_PACKAGE = BASE_PACKAGE + ".application..";
    private static final String APPLICATION_PORT_IN_PACKAGE = BASE_PACKAGE + ".application.port.in..";
    private static final String APPLICATION_PORT_OUT_PACKAGE = BASE_PACKAGE + ".application.port.out..";
    private static final String APPLICATION_SERVICE_PACKAGE = BASE_PACKAGE + ".application.service..";

    private static final String ADAPTER_IN_VAADIN_PACKAGE = BASE_PACKAGE + ".adapter.in.views..";
    private static final String ADAPTER_OUT_PERSISTENCE_PACKAGE = BASE_PACKAGE + ".adapter.out.persistence..";
    private static final String ADAPTER_OUT_API_PACKAGE = BASE_PACKAGE + ".adapter.out.api..";

    private static final String COMMON_PACKAGE = BASE_PACKAGE + ".common..";

    // Layer constants
    private static final String DOMAIN_LAYER = "Domain";
    private static final String APPLICATION_LAYER = "Application";
    private static final String ADAPTER_IN_LAYER = "AdapterIn";
    private static final String ADAPTER_OUT_LAYER = "AdapterOut";
    private static final String COMMON_LAYER = "Common";

    @Test
    @DisplayName("Domain Ports Should Be Interfaces")
    void domainPortsShouldBeInterfaces() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

        ArchRule rule = classes()
            .that().resideInAnyPackage(APPLICATION_PORT_IN_PACKAGE, APPLICATION_PORT_OUT_PACKAGE)
            .should().beInterfaces();

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Domain Models with 'Record' Suffix Should Be Records")
    void domainModelsWithRecordSuffixShouldBeRecords() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

        ArchRule rule = classes()
            .that().resideInAPackage(DOMAIN_MODEL_PACKAGE)
            .and().haveSimpleNameEndingWith("Record")
            .should().beRecords();

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Domain Models Should Not Depend On Spring or JPA")
    void domainModelsShouldNotDependOnFrameworks() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

        ArchRule rule = noClasses()
            .that().resideInAPackage(DOMAIN_MODEL_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework..",
                "javax.persistence..",
                "jakarta.persistence.."
            );

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Application Services Should Only Use Domain, Ports, and Spring Framework")
    void applicationServicesShouldUseDomainPortsAndSpring() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

        ArchRule rule = classes()
            .that().resideInAPackage(APPLICATION_SERVICE_PACKAGE)
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                APPLICATION_SERVICE_PACKAGE,
                APPLICATION_PORT_IN_PACKAGE,
                APPLICATION_PORT_OUT_PACKAGE,
                DOMAIN_PACKAGE,
                COMMON_PACKAGE,
                "org.springframework..",   // <-- Added
                "lombok..",                 // <-- Optional: Lombok annotations if you use @Slf4j, @RequiredArgsConstructor, etc.
                "java..",
                "org.slf4j.."
            );

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Only Service Implementations Should Be Annotated with @Service")
    void onlyServiceImplementationsShouldBeAnnotatedWithService() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

        ArchRule rule = classes()
            .that().areAnnotatedWith(org.springframework.stereotype.Service.class)
            .should().resideInAPackage(APPLICATION_SERVICE_PACKAGE);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("UI Should Not Depend On Persistence or External APIs")
    void uiShouldNotDependOnPersistenceOrExternalApis() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

        ArchRule rule = noClasses()
            .that().resideInAPackage(ADAPTER_IN_VAADIN_PACKAGE)
            .should().dependOnClassesThat().resideInAnyPackage(
                ADAPTER_OUT_PERSISTENCE_PACKAGE,
                ADAPTER_OUT_API_PACKAGE
            );

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Persistence Layer Should Only Be Accessed From Application Or Adapters")
    void persistenceLayerShouldOnlyBeAccessedFromApplicationOrAdapters() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

        ArchRule rule = noClasses()
            .that().resideOutsideOfPackages(APPLICATION_PACKAGE, ADAPTER_OUT_PERSISTENCE_PACKAGE)
            .should().accessClassesThat().resideInAnyPackage(ADAPTER_OUT_PERSISTENCE_PACKAGE);

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Layered Architecture Should Be Respected")
    void layeredArchitectureShouldBeRespected() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);

        ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer(DOMAIN_LAYER).definedBy(DOMAIN_PACKAGE)
            .layer(APPLICATION_LAYER).definedBy(APPLICATION_PACKAGE)
            .layer(ADAPTER_IN_LAYER).definedBy(ADAPTER_IN_VAADIN_PACKAGE)
            .layer(ADAPTER_OUT_LAYER).definedBy(ADAPTER_OUT_PERSISTENCE_PACKAGE, ADAPTER_OUT_API_PACKAGE)
            .layer(COMMON_LAYER).definedBy(COMMON_PACKAGE)

            // Define allowed dependencies
            .whereLayer(DOMAIN_LAYER).mayOnlyBeAccessedByLayers(APPLICATION_LAYER, ADAPTER_IN_LAYER, ADAPTER_OUT_LAYER)
            .whereLayer(APPLICATION_LAYER).mayOnlyBeAccessedByLayers(ADAPTER_IN_LAYER, ADAPTER_OUT_LAYER)
            .whereLayer(ADAPTER_IN_LAYER).mayNotBeAccessedByAnyLayer()
            .whereLayer(ADAPTER_OUT_LAYER).mayNotBeAccessedByAnyLayer()
            .whereLayer(COMMON_LAYER).mayOnlyBeAccessedByLayers(DOMAIN_LAYER, APPLICATION_LAYER, ADAPTER_IN_LAYER, ADAPTER_OUT_LAYER);

        rule.check(importedClasses);
    }
}