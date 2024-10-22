plugins {
    org.jetbrains.dokka // for dokkaGeneratePublicationHtml task
}

repositories {
    mavenCentral()
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
