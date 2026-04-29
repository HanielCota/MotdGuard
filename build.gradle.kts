import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask

buildscript {
    configurations.classpath {
        resolutionStrategy.force(
            "org.apache.logging.log4j:log4j-api:2.25.4",
            "org.apache.logging.log4j:log4j-core:2.25.4",
            "org.codehaus.plexus:plexus-utils:4.0.3",
        )
    }
}

plugins {
    java
    id("com.gradleup.shadow") version "9.4.1"
    id("com.github.spotbugs") version "6.5.1"
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
        "org.apache.logging.log4j:log4j-api:2.25.4",
        "org.apache.logging.log4j:log4j-core:2.25.4",
        "org.codehaus.plexus:plexus-utils:4.0.3",
    )
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")

    implementation("co.aikar:acf-velocity:0.5.1-SNAPSHOT")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")
    implementation("com.bucket4j:bucket4j_jdk17-core:8.18.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.21.3")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
}

spotbugs {
    toolVersion.set("4.9.8")
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.DEFAULT)
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
        reports {
            create("html") {
                required.set(true)
                outputLocation.set(layout.buildDirectory.file("reports/spotbugs.html"))
            }
        }
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
