import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

plugins {
    org.jetbrains.dokka // for dokkaHtmlMultiModule task
}

repositories {
    mavenCentral()
}

group = Library.group
version = Library.version

// To upgrade Gradle, run the following command twice and commit all changes:
// ./gradlew wrapper --gradle-version <version> --gradle-distribution-sha256-sum <checksum>
// (use 'Complete (-all) ZIP Checksum' from https://gradle.org/release-checksums for <checksum>)
tasks.wrapper {
    distributionType = ALL
}
