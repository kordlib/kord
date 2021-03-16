import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

sourceSets {
    val samples by creating {
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
}

configurations {
    val samplesImplementation by getting {
        extendsFrom(configurations["implementation"])
    }
}

dependencies {
    api(common)

    api(Dependencies.`ktor-client-json`)
    api(Dependencies.`ktor-client-json-jvm`)
    api(Dependencies.`ktor-client-websocket`)
    api(Dependencies.`ktor-client-serialization-jvm`)
    api(Dependencies.`ktor-client-cio`)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = Jvm.target
        freeCompilerArgs = listOf(
                CompilerArguments.inlineClasses,
                CompilerArguments.coroutines,
                CompilerArguments.time,
                CompilerArguments.optIn
        )
    }
}
