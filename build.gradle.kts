plugins {
    org.jetbrains.dokka // for dokkaHtmlMultiModule task
}

repositories {
    mavenCentral()
}

group = Library.group
version = Library.version

dependencies {
    dokkaHtmlMultiModulePlugin(libs.dokka.versioning.plugin)
}

tasks {
    val oldDocsRoot = "docs-cache"
    dokkaHtmlMultiModule {
        applyVersioningPlugin()
    }

    val saveDocs = task<Copy>("saveVersionedDocs") {
        fromDokkaMutliModule()
        if (Library.isSnapshot) {
            into("$oldDocsRoot/current")
        } else {
            into("$oldDocsRoot/${Library.version}")
        }
    }

    task<Copy>("produceDocs") {
        dependsOn(saveDocs)
        // copy old versions
        from(oldDocsRoot) {
            exclude("current")
        }
        // copy up to date version
        fromDokkaMutliModule {
            if (Library.isRelease) {
                into(Library.version)
            }
        }
        into("docs")
    }
}

// For some reason from(dokkaHtmlMultiModule) produces some really weird output
inline fun Copy.fromDokkaMutliModule(crossinline configure: CopySpec.() -> Unit = {}) {
    from("$buildDir/dokka/htmlMultiModule") {
        configure()
    }
    dependsOn(tasks.dokkaHtmlMultiModule)
}
