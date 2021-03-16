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
    api(rest)
    api(gateway)

    api(Dependencies.`cache-api`) {
        version {
            strictly("0.3.0-SNAPSHOT")
        }
    }

    api(Dependencies.`cache-map`) {
        version {
            strictly("0.3.0-SNAPSHOT")
        }
    }

    testImplementation(Dependencies.mockk)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = Jvm.target
        freeCompilerArgs = listOf(
                CompilerArguments.inlineClasses,
                CompilerArguments.coroutines,
                CompilerArguments.time,
                CompilerArguments.stdLib,
                CompilerArguments.optIn
        )
    }
}
