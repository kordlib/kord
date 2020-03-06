# Kord Core

Kord `core` is an implementation of the discord api build on top of the [gateway](https://gitlab.com/kordlib/kord/tree/master/gateway), 
[rest](https://gitlab.com/kordlib/kord/tree/master/rest) and [cache](https://gitlab.com/kordlib/cache) modules. It features a high level representation of Discord's entities and their behaviour
in a non-blocking, coroutine focused, event-driven design.

## Example usage

```kotlin
suspend fun main() {
    val kord = Kord(token)

    // Flow style
    kord.events.buffer(CoroutineChannel.UNLIMITED)
            .filterIsInstance<MessageCreateEvent>()
            .filter { it.message.author?.isBot == false }
            .filter { it.message.content == "!ping" }
            .onEach { it.message.channel.createMessage("pong") }
            .launchIn(kord)

    // Simplified style
    kord.on<MessageCreateEvent> {
        if(message.author?.isBot == true) return@on
        if (message.content == "!ping") message.channel.createMessage("pong")
    }

    kord.login()
}
```

## Installation

Replace `{version}` with the latest version number on bintray [ ![Download](https://api.bintray.com/packages/kordlib/Kord/Kord/images/download.svg) ](https://bintray.com/kordlib/Kord/Kord/_latestVersion).

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
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
    <groupId>com.gitlab.kordlib.kord</groupId>
    <artifactId>core</artifactId>
    <version>{version}</version>
</dependency>
```