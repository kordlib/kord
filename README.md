# Kord
[![Discord](https://img.shields.io/discord/556525343595298817.svg?color=&label=Kord&logo=discord&style=for-the-badge)](https://discord.gg/6jcx5ev)
    [![JitPack](https://img.shields.io/jitpack/v/gitlab/hopebaron/Kord.svg?color=&style=for-the-badge)](https://jitpack.io/#com.gitlab.kordlib/Kord)
[![Gitlab pipeline status (branch)](https://img.shields.io/gitlab/pipeline/HopeBaron/kord/master.svg?style=for-the-badge)]()

**Kord is still in its early stages, meaning it's not ready to be used for public purposes yet.**


Kord is an idiomatic, non-blocking, modularized implementation of the Discord API. 

# Goals

## No blocking, no callbacks

Build on top of coroutines, Kord focusses on avoiding the pitfalls of java libraries without sacrificing performance.

```kotlin
suspend fun main() {
    val kord = Kord("token")

    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        val guild = message.getGuild() ?: return@on

        if (message.content == "!kord") message.channel.createEmbed {
            author {
                val owner = kord.getApplicationInfo().getOwner()
                name = owner.username
                icon = owner.avatar.url
            }

            description = "an embed made with kord"

            field {
                name = "guild description"
                value = guild.description.orEmpty()
            }

            footer {
                url = "https://gitlab.com/kordlib/kord"
                text = "made with kord"
                icon = "https://assets.gitlab-static.net/uploads/-/system/project/avatar/11355644/cord-icon.png?width=64"
            }

            guild.getOwner().displayName
        }
    }

    kord.login()
}
```

## More than just an API wrapper

Kord aims to be more than just an event dispatcher, Live objects offer a style of discord bots that focus on state changes instead of event creation.

//TODO add code example

## Modular, extensible

In its goal to become a comprehensive wrapper for the Discord API, Kord allows each segment of the API to be a standalone library, able to be swapped out for other implementations, and focusses heavily on its configuration.

## Testable

In an effort to ease the pain of bot development, Kord grants the user an extensive testing framework. Easily build your own test cases and execute them as if you're working with the live Discord API.

//TODO add code example



## Installation

### Gradle

```groovy
repositories {
 ...
 jcenter()
 maven { url 'https://jitpack.io' }
 maven { url "https://dl.bintray.com/kordlib/Kord" }
}
```

```groovy
dependencies {
 ...
 implementation 'com.gitlab.kordlib.kord:kord-core:0.2.3'
}
```

### Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
<repository>
    <id>bintray</id>
    <url>https://dl.bintray.com/kordlib/Kord</url>
</repository>
```

```xml
<dependency>
  <groupId>com.gitlab.kordlib.kord</groupId>
  <artifactId>kord-core</artifactId>
  <version>0.2.3</version>
  <type>pom</type>
</dependency>
```

