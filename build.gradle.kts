plugins {
    org.jetbrains.dokka // for dokkaHtmlMultiModule task
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

group = Library.group
version = libraryVersion
