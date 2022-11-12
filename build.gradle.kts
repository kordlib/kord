import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

plugins {
    org.jetbrains.dokka // for dokkaHtmlMultiModule task
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
