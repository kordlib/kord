import com.vanniktech.maven.publish.JavaPlatform

plugins {
    `java-platform`
    id("com.vanniktech.maven.publish.base")
}

val me = project
rootProject.subprojects {
    if (name != me.name) {
        me.evaluationDependsOn(path)
    }
}

dependencies {
    constraints {
        rootProject.subprojects.forEach { subproject ->
            if (subproject.plugins.hasPlugin("maven-publish") && subproject.name != name) {
                subproject.publishing.publications.withType<MavenPublication>().configureEach {
                    if (!artifactId.endsWith("-metadata") &&
                        !artifactId.endsWith("-kotlinMultiplatform")
                    ) {
                        api("$groupId:$artifactId:$version")
                    }
                }
            }
        }
    }
}

mavenPublishing {
    configure(JavaPlatform())
}

apply(plugin = "kord-publishing")
