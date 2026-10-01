package com.nexo.ecommerce.auth.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.nexo.ecommerce.auth")
class CleanArchitectureTest {
    @ArchTest
    static final ArchRule domain_and_application_do_not_depend_on_frameworks =
            noClasses()
                    .that().resideInAnyPackage("..domain..", "..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..", "jakarta.persistence..",
                            "jakarta.validation..", "io.jsonwebtoken..");

    @ArchTest
    static final ArchRule domain_does_not_depend_on_application_or_adapters =
            noClasses()
                    .that().resideInAnyPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..application..", "..infrastructure..", "..presentation..");

    @ArchTest
    static final ArchRule application_does_not_depend_on_outer_adapters =
            noClasses()
                    .that().resideInAnyPackage("..application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..", "..presentation..");
}
