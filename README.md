# Kord
[![Discord](https://img.shields.io/discord/556525343595298817.svg?color=&label=Kord&logo=discord&style=for-the-badge)](https://discord.gg/9VTsymm)
[![JitPack](https://img.shields.io/jitpack/v/gitlab/hopebaron/Kord.svg?color=&style=for-the-badge)](https://jitpack.io/#com.gitlab.hopebaron/Kord)
[![Gitlab pipeline status (branch)](https://img.shields.io/gitlab/pipeline/HopeBaron/kord/master.svg?style=for-the-badge)]()


Kord is an idiomatic, non-blocking, modularized implementation of the Discord API. 

#Goals

## No blocking, no callbacks

Build on top of coroutines, Kord focusses on avoiding the pitfalls of java libraries without sacrificing performance.

//TODO add code example

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
 maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
 ...
 implementation 'com.gitlab.hopebaron:Kord:0.0.1'
}
```

### Maven

```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
    <groupId>com.gitlab.hopebaron</groupId>
	<artifactId>Kord</artifactId>
	<version>0.0.1</version>
</dependency>
```

