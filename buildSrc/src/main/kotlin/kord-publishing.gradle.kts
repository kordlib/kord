import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.KotlinMultiplatform
import dev.kord.gradle.tools.util.libraryVersion

plugins {
    com.vanniktech.maven.publish
    dev.kord.`gradle-tools`
}

mavenPublishing {
    coordinates(Library.group, "kord-${project.name}", libraryVersion)

    publishToMavenCentral()
    signAllPublications()

    pom {
        name = Library.name
        description = Library.description
        url = Library.projectUrl

        organization {
            name = "Kord"
            url = "https://github.com/kordlib"
        }

        developers {
            developer {
                name = "The Kord Team"
            }
        }

        issueManagement {
            system = "GitHub"
            url = "https://github.com/kordlib/kord/issues"
        }

        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        scm {
            connection = "scm:git:ssh://github.com/kordlib/kord.git"
            developerConnection = "scm:git:ssh://git@github.com:kordlib/kord.git"
            url = Library.projectUrl
        }
    }

    if (plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
        configure(KotlinMultiplatform(JavadocJar.Dokka("dokkaGeneratePublicationHtml"), sourcesJar = true))
    } else if(plugins.hasPlugin("org.jetbrains.kotlin.jvm")) {
        configure(KotlinJvm(JavadocJar.Dokka("dokkaGeneratePublicationHtml"), sourcesJar = true))
    }
}
