plugins {
    org.jetbrains.dokka // for dokkaHtmlMultiModule task
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

group = Library.group
version = Library.version
