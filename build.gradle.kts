plugins {
    org.jetbrains.dokka // for dokkaGeneratePublicationHtml task
}

allprojects {
    repositories {
        // TODO: Remove once Ktor 3.1.0 releases
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap/")
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

group = Library.group

dependencies {
    dokka(projects.common)
    dokka(projects.core)
    dokka(projects.coreVoice)
    dokka(projects.gateway)
    dokka(projects.rest)
    dokka(projects.voice)
}
