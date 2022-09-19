import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

@Suppress("DSL_SCOPE_VIOLATION") // false positive for `libs` in IntelliJ
plugins {
    org.jetbrains.dokka

    signing
    `maven-publish`
    alias(libs.plugins.nexusStaging)
}

repositories {
    mavenCentral()
}

group = Library.group
version = Library.version

tasks.wrapper {
    gradleVersion = libs.versions.gradle.get()
    distributionType = ALL
    distributionSha256Sum = libs.versions.gradleChecksum.get()
}
