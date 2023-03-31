plugins {
    `kord-module`
    `kord-publishing`
}

dependencies {
    api(projects.core)
    api(projects.voice)

    ksp(projects.kspProcessors)
}
