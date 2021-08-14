# Kord

[![Discord](https://img.shields.io/discord/556525343595298817.svg?color=&label=Kord&logo=discord&style=for-the-badge)](https://discord.gg/6jcx5ev)
[![Download](https://img.shields.io/maven-central/v/dev.kord/kord-core.svg?label=Maven%20Central&style=for-the-badge)](https://search.maven.org/search?q=g:%22dev.kord%22%20AND%20a:%22kord-core%22)
[![Github CI status (branch)](https://img.shields.io/github/workflow/status/kordlib/kord/CI/master?label=CI&style=for-the-badge)]()

__Kord is still in an experimental stage, as such we can't guarantee API stability between releases. While we'd love for
you to try out our library, we don't recommend you use this in production just yet.__

If you have any feedback, we'd love to hear it, hit us up on discord or write up an issue if you have any suggestions!

## What is Kord

Kord is a [coroutine-based](https://kotlinlang.org/docs/reference/coroutines-overview.html), modularized implementation
of the Discord API, written 100% in [Kotlin](https://kotlinlang.org/).

## Why use Kord

Kord was created as an answer to the frustrations of writing Discord bots with other JVM libraries, which either use
thread-blocking code or verbose and scope restrictive reactive systems. We believe an API written from the ground up in
Kotlin with coroutines can give you the best of both worlds: The conciseness of imperative code with the concurrency of
reactive code.

Aside from coroutines, we also wanted to give the user full access to lower level APIs. Sometimes you have to do some
unconventional things, and we want to allow you to do those in a safe and supported way.

## Status of Kord

* [X] [Discord Gateway](https://github.com/kordlib/kord/tree/master/gateway)
* [x] [Discord Rest API](https://github.com/kordlib/kord/tree/master/rest)
* [X] [High level abstraction + caching](https://github.com/kordlib/kord/tree/master/core)
* [X] Discord Voice
* [ ] Support for multiple processes #7

Right now Kord *should* provide a full mapping of the non-voice API. We're currently working on a testing library for
easy bot testing against a semi mocked client as well as our own command system to facilitate more complex bot
development.

## Documentation

* [Dokka docs](https://kordlib.github.io/kord/)
* [Wiki](https://github.com/kordlib/kord/wiki)

## Installation

Replace `{version}` with the latest version number on maven central.

For Snapshots replace `{version}` with `{branch}-SNAPSHOT` 

e.g: `0.7.x-SNAPSHOT`

[![Download](https://img.shields.io/maven-central/v/dev.kord/kord-core.svg?label=Maven%20Central&style=for-the-badge)](https://search.maven.org/search?q=g:%22dev.kord%22%20AND%20a:%22kord-core%22)

### Gradle (groovy)

```groovy
repositories {
    mavenCentral()
    // Kord Snapshots Repository (Optional):
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots"
    }

}
```

```groovy
dependencies {
    implementation("dev.kord:kord-core:{version}")
}
```

### Gradle (kotlin)

```kotlin
repositories {
    mavenCentral()
    // Kord Snapshots Repository (Optional):
    maven("https://oss.sonatype.org/content/repositories/snapshots")

}
```

---

```kotlin
dependencies {
    implementation("dev.kord:kord-core:{version}")
}
```

### Maven

##### Kord Snapshots Repository (Optional):

```xml

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
    <artifactId>kord-core</artifactId>
    <version>{version}</version>
</dependency>
```

## Modules

### Core

A higher level API, combining `rest` and `gateway`, with additional (optional) caching. Unless you're writing your own
abstractions, we'd recommend using this.

```kotlin
suspend fun main() {
    val client = Kord("your bot token")
    val pingPong = ReactionEmoji.Unicode("\uD83C\uDFD3")

    client.on<MessageCreateEvent> {
        if (message.content != "!ping") return@on

        val response = message.channel.createMessage("Pong!")
        response.addReaction(pingPong)

        delay(5000)
        message.delete()
        response.delete()
    }

    client.login()
}
```

### Rest

A low level mapping of Discord's REST API. Requests follow
Discord's [rate limits](https://discordapp.com/developers/docs/topics/rate-limits).

```kotlin
suspend fun main() {
    val rest = RestClient("your bot token")

    rest.channel.createMessage("605212557522763787") {
        content = "Hello Kord!"

        embed {
            color = Color.BLUE
            description = "Hello embed!"
        }
    }

}
```

### Gateway

A low level mapping of [Discord's Gateway](https://discordapp.com/developers/docs/topics/gateway), which maintains the
connection and rate limits commands.

```kotlin
suspend fun main() {
    val gateway = DefaultGateway()

    gateway.on<MessageCreate> {
        println("${message.author.username}: ${message.content}")
        val words = message.content.split(' ')
        when (words.firstOrNull()) {
            "!close" -> gateway.stop()
            "!detach" -> gateway.detach()
        }
    }.launchIn(gateway)

    gateway.start("your bot token")
}
```

## FAQ

## Will you support kotlin multi-platform

We will, there's an [issue](https://github.com/kordlib/kord/issues/69) open to track the features we want/need to make a
transition to MPP smooth.

## When will you document your code

Yes.
