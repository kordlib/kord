import org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

plugins {
    org.jetbrains.dokka // for dokkaHtmlMultiModule task
}

repositories {
    mavenCentral()
    // until Dokka 1.8.0 is released and we no longer need dev builds, see https://github.com/kordlib/kord/pull/755
    maven("https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
}

group = Library.group
version = Library.version

// To upgrade Gradle, run the following command twice and commit all changes:
// ./gradlew wrapper --gradle-version <version> --gradle-distribution-sha256-sum <checksum>
// (use 'Complete (-all) ZIP Checksum' from https://gradle.org/release-checksums for <checksum>)
tasks.wrapper {
    distributionType = ALL
}
