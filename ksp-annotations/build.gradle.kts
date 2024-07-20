import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask

plugins {
    `kord-internal-multiplatform-module`

    // workaround for https://youtrack.jetbrains.com/issue/KT-43500 /
    // https://youtrack.jetbrains.com/issue/KT-64109#focus=Comments-27-10064206.0-0 /
    // https://youtrack.jetbrains.com/issue/KT-61096 (not intended to be published)
    org.jetbrains.dokka
    `kord-publishing`
}

tasks.withType<AbstractDokkaLeafTask>().configureEach {
    dokkaSourceSets.configureEach {
        suppress = true
    }
}
