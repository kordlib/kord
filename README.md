# Kord

[![Discord](https://img.shields.io/discord/556525343595298817.svg?color=&label=Kord&logo=discord&style=for-the-badge)](https://discord.gg/6jcx5ev)
[![Download](https://img.shields.io/maven-central/v/dev.kord/kord-core.svg?label=Maven%20Central&style=for-the-badge)](https://central.sonatype.com/artifact/dev.kord/kord-core)
[![Github CI status (branch)](https://img.shields.io/github/actions/workflow/status/kordlib/kord/deployment-ci.yml?branch=main&label=CI&style=for-the-badge)]()

Kord is a [coroutine-based](https://kotlinlang.org/docs/reference/coroutines-overview.html), modularized implementation
of the Discord API, written 100% in [Kotlin](https://kotlinlang.org/).

If you have any feedback, we'd love to hear it, hit us up on discord or write up an issue if you have any suggestions!

## Why use Kord

Kord was created as an answer to the frustrations of writing Discord bots with other JVM libraries, which either use
thread-blocking code or verbose and scope restrictive reactive systems. We believe an API written from the ground up in
Kotlin with coroutines can give you the best of both worlds: The conciseness of imperative code with the concurrency of
reactive code.

Aside from coroutines, we also wanted to give the user full access to lower level APIs. Sometimes you have to do some
unconventional things, and we want to allow you to do those in a safe and supported way.

## Status of Kord

* [X] [Discord Gateway](gateway)
* [x] [Discord Rest API](rest)
* [X] [High level abstraction + caching](core)
* [X] [Discord Voice](voice)
* [ ] Support for multiple processes [#7](https://github.com/kordlib/kord/issues/7)

Right now, Kord *should* provide a full mapping of the non-voice API on Kotlin/JVM and Kotlin/JS and an experimental
mapping of the Voice API on Kotlin/JVM

## Documentation

* [Dokka docs](https://kordlib.github.io/kord/)
* [Wiki](https://github.com/kordlib/kord/wiki)

## Modules

| Module                   | Docs                                                    | Artifact           | JVM | JS (NodeJS) | Native |
|--------------------------|---------------------------------------------------------|--------------------|-----|-------------|--------|
| [common](common)         | [common](https://kordlib.github.io/kord/common)         | `kord-common`¹     | ✅   | ✅           | ✅      |
| [rest](rest)             | [rest](https://kordlib.github.io/kord/rest)             | `kord-rest`¹       | ✅   | ✅           | ✅      |
| [gateway](gateway)       | [gateway](https://kordlib.github.io/kord/gateway)       | `kord-gateway`¹    | ✅   | ✅           | ✅      |
| [core](core)             | [core](https://kordlib.github.io/kord/core)             | `kord-core`¹       | ✅   | ✅           | ✅      |
| [voice](voice)           | [voice](https://kordlib.github.io/kord/voice)           | `kord-voice`¹      | ✅   | ✅           | ✅²     |
| [core-voice](core-voice) | [core-voice](https://kordlib.github.io/kord/core-voice) | `kord-core-voice`¹ | ✅   | ✅           | ✅²     |

¹ These artifacts only supports Gradle Version 5.3 or higher, for older Gradle versions and Maven please append `-jvm`  
² Currently not on Windows(MinGW) targets, see #69  

## Installation

Replace `{version}` with the latest version number on maven central.

For Snapshots replace `{version}` with `{branch}-SNAPSHOT`

e.g: `feature-amazing-thing-SNAPSHOT` for the branch `feature/amazing-thing`

For Snapshots for the branch `main` replace `{version}` with `{nextPlannedVersion}-SNAPSHOT` (see `nextPlannedVersion`
in [`gradle.properties`](gradle.properties))

[![Download](https://img.shields.io/maven-central/v/dev.kord/kord-core.svg?label=Maven%20Central&style=for-the-badge)](https://central.sonatype.com/artifact/dev.kord/kord-core)

### Gradle (Kotlin)

```kotlin
repositories {
    mavenCentral()
    // Kord Snapshot Repositories (optional, choose one of these):
    maven("https://repo.kord.dev/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation("dev.kord:kord-core:{version}")
}
```

### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
    // Kord Snapshot Repositories (optional, choose one of these):
    maven {
        url "https://repo.kord.dev/snapshots"
    }
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }
}

dependencies {
    implementation("dev.kord:kord-core:{version}")
}
```

### Maven

##### Kord Snapshot Repositories (optional, choose one of these):

```xml
<repository>
    <id>snapshots-repo</id>
    <url>https://repo.kord.dev/snapshots</url>
    <releases>
        <enabled>false</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>

<repository>
    <id>snapshots-repo</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
        <enabled>false</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

---

```xml
<dependency>
    <groupId>dev.kord</groupId>
    <artifactId>kord-core-jvm</artifactId>
    <version>{version}</version>
</dependency>
```

## FAQ

## Will you support kotlin multi-platform

Currently we're supporting both Kotlin/JVM and Kotlin/JS for the majority of our API, for more information check
[Modules](#modules) and #69

## When will you document your code?

Yes.

# This project is supported by JetBrains

[![JetBrains Logo (Main) logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://jb.gg/OpenSourceSupport)
