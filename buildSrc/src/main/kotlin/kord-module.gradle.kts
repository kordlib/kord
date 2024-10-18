plugins {
    org.jetbrains.kotlin.jvm
    org.jetbrains.kotlin.plugin.serialization
    org.jetbrains.dokka
    org.jetbrains.kotlinx.atomicfu
    org.jetbrains.kotlinx.`binary-compatibility-validator`
    com.google.devtools.ksp
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    ksp(project(":ksp-processors"))
}

apiValidation {
    applyKordBCVOptions()
}

kotlin {
    explicitApi()
    compilerOptions {
        applyKordJvmCompilerOptions()
        optIn.addAll(kordOptIns)
    }

    sourceSets {
        applyKordTestOptIns()
    }
}

dokka {
    applyKordDokkaOptions(project)
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<JavaCompile>().configureEach {
        options.release = KORD_JVM_TARGET
    }
}

publishing {
    publications.register<MavenPublication>(Library.name) {
        from(components["java"])
        artifact(tasks.kotlinSourcesJar)
    }
}
