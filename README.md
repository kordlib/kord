# Kord

[![Discord](https://img.shields.io/discord/556525343595298817.svg?color=&label=Kord&logo=discord&style=for-the-badge)](https://discord.gg/6jcx5ev)
[![Download](https://img.shields.io/bintray/v/kordlib/Kord/Kord?color=&style=for-the-badge) ](https://bintray.com/kordlib/Kord/Kord/_latestVersion)
[![JitPack](https://img.shields.io/jitpack/v/gitlab/hopebaron/Kord.svg?color=&style=for-the-badge)](https://jitpack.io/#com.gitlab.kordlib/Kord)
[![Gitlab pipeline status (branch)](https://img.shields.io/gitlab/pipeline/HopeBaron/kord/master.svg?style=for-the-badge)]()

__Kord is still in an experimental stage, as such we can't guarantee API stability between releases.
While we'd love for you to try out our library, we don't recommend you use this in production just yet.__

If you have any feedback, we'd love to hear it, hit us up on discord or write up an issue if you have any suggestions!

## What is Kord

Kord is a [coroutine](https://kotlinlang.org/docs/reference/coroutines-overview.html)-based, modularized implementation of the Discord API, written 100% in [Kotlin](https://kotlinlang.org/).

## Why use Kord

Kord was created as an answer to the frustrations of writing Discord bots with other JVM libraries, which either use thread-blocking code or verbose and scope restrictive reactive systems.
We believe an API written from the ground up in Kotlin with coroutines can give you the best of both worlds: The conciseness of imperative code with the concurrency of reactive code.

Aside from coroutines, we also wanted to give the user full access to lower level APIs.
Sometimes you have to do some unconventional things, and we want to allow you to do those in a safe and supported way.

## Status of Kord

* [X] [Discord Gateway](https://gitlab.com/kordlib/kord/-/tree/master/gateway)
* [x] [Discord Rest API](https://gitlab.com/kordlib/kord/-/tree/master/rest)
* [X] [High level abstraction + caching](https://gitlab.com/kordlib/kord/-/tree/master/core)
* [ ] [Test Framework](https://gitlab.com/kordlib/kordx.test) (WIP) #49
* [ ] [Command Framework](https://gitlab.com/kordlib/kordx.commands) (WIP) #36
* [ ] Discord Voice
* [ ] Support for multiple processes #55

Right now Kord *should* provide a full mapping of the non-voice API.
We're currently working on a testing library for easy bot testing against a semi mocked client as well as our own command system to facilitate more complex bot development.

## Installation

Replace `{version}` with the latest version number on bintray. [![Download](https://img.shields.io/bintray/v/kordlib/Kord/Kord?color=&style=for-the-badge) ](https://bintray.com/kordlib/Kord/Kord/_latestVersion)

### Gradle (groovy)

```groovy
repositories {
    maven {
        url = "https://dl.bintray.com/kordlib/Kord"
    }
}
```

```groovy
dependencies {
   implementation("com.gitlab.kordlib.kord:kord-core:{version}")
}
```

### Gradle (kotlin)

```kotlin
repositories {
   maven(url = "https://dl.bintray.com/kordlib/Kord")
}
```

```kotlin
dependencies {
   implementation("com.gitlab.kordlib.kord:kord-core:{version}")
}
```

### Maven

```xml
<repository>
    <id>bintray-kord</id>
    <url>https://dl.bintray.com/kordlib/Kord</url>
</repository>
```

```xml
<dependency>
    <groupId>com.gitlab.kordlib.kord</groupId>
    <artifactId>kord-core</artifactId>
    <version>{version}</version>
</dependency>
```

## Modules

### Core

A higher level API, combining `rest` and `gateway`, with additional (optional) caching.
Unless you're writing your own abstractions, we'd recommend using this.

```kotlin
suspend fun main() {
    val client = Kord("your bot token")
    val pingPong = ReactionEmoji.Unicode("\uD83C\uDFD3")

    client.on<MessageCreateEvent> {
        if(message.content != "!ping") return@on

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

A low level mapping of Discord's REST API. Requests follow Discord's [rate limits](https://discordapp.com/developers/docs/topics/rate-limits).

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

A low level mapping of [Discord's Gateway](https://discordapp.com/developers/docs/topics/gateway), which maintains the connection and rate limits commands.

```kotlin
suspend fun main() {
    val gateway = DefaultGateway()

    gateway.events.filterIsInstance<MessageCreate>()
            .flowOn(Dispatchers.IO)
            .onEach {
                when (it.message.content) {
                    "!close" -> gateway.stop()
                    "!restart" -> gateway.restart()
                    "!detach" -> gateway.detach()
                }
            }
            .launchIn(GlobalScope)

    gateway.start("your bot token")
}
```

## FAQ

### Will you support kotlin multi-platform

We originally intended to. We exclusively depend on multi-platform libraries and try to minimize JVM exclusive code.
However, we've found support for multi-platform and non-JVM platforms to be lacking (too experimental).
We'll revisit multi-platform support once IR (intermediate representation) has been implemented, or our library is feature complete, whichever comes first.

### Will you publish your kotlin docs online

The only real documentation engine for kotlin is [Dokka](https://github.com/Kotlin/dokka).
It's currently not in a happy place, but we've been told it'll get a rather extensive release sooner than later, so we're holding off until that happens.
