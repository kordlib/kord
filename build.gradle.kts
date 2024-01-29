plugins {
    org.jetbrains.dokka // for dokkaHtmlMultiModule task
}

allprojects {
    repositories {
        // TODO: Remove wants https://github.com/ktorio/ktor/pull/3950 lands
        maven("https://europe-west3-maven.pkg.dev/mik-music/kord")
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

group = Library.group
version = libraryVersion
