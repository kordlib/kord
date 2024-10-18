plugins {
    org.jetbrains.dokka // for dokkaGeneratePublicationHtml task
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
}

group = Library.group
version = libraryVersion

dependencies {
    dokka(projects.common)
    dokka(projects.core)
    dokka(projects.coreVoice)
    dokka(projects.gateway)
    dokka(projects.rest)
    dokka(projects.voice)
}
