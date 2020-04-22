import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
                CompilerArguments.unstableDefault
        )
    }
}
