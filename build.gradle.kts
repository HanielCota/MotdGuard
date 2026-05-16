import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask

buildscript {
    configurations.classpath {
        resolutionStrategy.force(
            "org.apache.logging.log4j:log4j-api:2.26.0",
            "org.apache.logging.log4j:log4j-core:2.26.0",
            "org.codehaus.plexus:plexus-utils:4.0.3",
        )
    }
}

plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.spotbugs)
    alias(libs.plugins.spotless)
}

group = "io.github.hanielcota"
version = "1.0.0"

description = "Dynamic MOTD plugin for Velocity with maintenance mode and rate limiting"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/") {
        name = "aikar"
    }
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
}

configurations.configureEach {
    resolutionStrategy.force(
        "org.apache.logging.log4j:log4j-api:2.26.0",
        "org.apache.logging.log4j:log4j-core:2.26.0",
        "org.codehaus.plexus:plexus-utils:4.0.3",
    )
}

dependencies {
    compileOnly(libs.velocity.api)
    annotationProcessor(libs.velocity.api)

    implementation(libs.acf.velocity)
    implementation(libs.caffeine)
    implementation(libs.bucket4j)
    implementation(libs.jackson.toml)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.mockito.core)
    testImplementation(libs.velocity.api)

    spotbugsPlugins(libs.spotbugs.plugin)
}

spotbugs {
    toolVersion.set(libs.versions.spotbugs.get())
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.DEFAULT)
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat(libs.versions.google.java.format.get())
        formatAnnotations()
    }
}

tasks {
    compileJava {
        options.release.set(21)
        options.compilerArgs.add("-parameters")
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("co.aikar.commands", "io.github.hanielcota.motdguard.libs.acf")
        relocate("co.aikar.locales", "io.github.hanielcota.motdguard.libs.locales")
        relocate("com.github.benmanes.caffeine", "io.github.hanielcota.motdguard.libs.caffeine")
        relocate("io.github.bucket4j", "io.github.hanielcota.motdguard.libs.bucket4j")
        relocate("com.fasterxml.jackson", "io.github.hanielcota.motdguard.libs.jackson")
    }

    withType<SpotBugsTask> {
        excludeFilter.set(file("spotbugs-exclude.xml"))
        reports {
            create("html") {
                required.set(true)
                outputLocation.set(layout.buildDirectory.file("reports/spotbugs.html"))
            }
        }
    }

    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }

    register("printVersion") {
        group = "help"
        description = "Prints the project version."
        doLast {
            println(project.version)
        }
    }
}