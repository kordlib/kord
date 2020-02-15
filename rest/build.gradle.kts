dependencies {
    api(common)

    api(Dependencies.`ktor-client-json`)
    api(Dependencies.`ktor-client-json-jvm`)
    api(Dependencies.`ktor-client-serialization-jvm`)
    api(Dependencies.`ktor-client-cio`)

    testImplementation(Dependencies.`ktor-client-mock`)
    testImplementation(Dependencies.`ktor-client-mock-jvm`)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = Jvm.target
        freeCompilerArgs = listOf(
                CompilerArguments.inlineClasses,
                CompilerArguments.coroutines,
                CompilerArguments.time
        )
    }
}
