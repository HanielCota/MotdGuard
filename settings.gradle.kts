plugins {
    // Enables automatic provisioning of the Java 21 toolchain on machines that do not
    // have a matching JDK installed (downloads a Temurin JDK once via Foojay).
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "motdguard"
