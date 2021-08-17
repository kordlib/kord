plugins {
    kotlin("jvm")
}

sourceSets {
    create("samples") {
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
}

configurations {
    getByName("samplesImplementation") {
        extendsFrom(configurations["implementation"])
    }
}
