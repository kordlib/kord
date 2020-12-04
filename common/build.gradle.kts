import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
