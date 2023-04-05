import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask

plugins {
    `kord-internal-multiplatform-module`

    // workaround for https://youtrack.jetbrains.com/issue/KT-43500 (not intended to be published)
    org.jetbrains.dokka
    `kord-publishing`
}

tasks.withType<AbstractDokkaLeafTask>().configureEach {
    enabled = false
}
