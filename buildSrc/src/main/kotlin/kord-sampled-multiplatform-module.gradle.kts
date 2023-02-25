plugins {
    org.jetbrains.kotlin.multiplatform
}

kotlin {
    sourceSets {
        create("samples") {
            dependsOn(getByName("jvmMain"))
        }
    }
}
