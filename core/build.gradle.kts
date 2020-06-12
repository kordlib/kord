import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(Plugins.kapt)
}

dependencies {
    api(common)
    api(rest)
    api(gateway)

    api(Dependencies.`cache-api`) {
        version {
            strictly("[0.1.0, 0.2.0[")
            prefer("latest.release")
        }
    }

    api(Dependencies.`cache-map`) {
        version {
            strictly("[0.1.0, 0.2.0[")
            prefer("latest.release")
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
                CompilerArguments.time
        )
    }
}
