plugins {
    org.jetbrains.dokka // for dokkaHtmlMultiModule task
}

allprojects {
    repositories {
        mavenCentral()
    }
}

group = Library.group
version = Library.version
