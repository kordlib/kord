# Kord Core

Kord `core` is an implementation of the discord api build on top of the [gateway](../gateway), [rest](../rest) and
[cache](https://github.com/kordlib/cache) modules. It features a high level representation of Discord's entities and
their behavior in a non-blocking, coroutine focused, event-driven design.

## Example usage

```kotlin
suspend fun main() {
    val kord = Kord("your bot token")

    // Flow style
    kord.events
        .filterIsInstance<MessageCreateEvent>()
        .map { it.message }
        .filter { it.author?.isBot == false }
        .filter { it.content == "!ping" }
        .onEach { it.channel.createMessage("pong") }
        .launchIn(kord)

    // Simplified style
    kord.on<MessageCreateEvent> {
        if (message.author?.isBot == true) return@on
        if (message.content == "!ping") message.channel.createMessage("pong")
    }

    kord.login {
        @OptIn(PrivilegedIntent::class)
        intents += Intent.MessageContent
    }
}
```

## Installation

See the root [README](../README.md#installation) for more information.

### Gradle (Kotlin)

```kotlin
dependencies {
    implementation("dev.kord:kord-core:{version}")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation("dev.kord:kord-core:{version}")
}
```

### Maven

```xml
<dependency>
    <groupId>dev.kord</groupId>
    <artifactId>kord-core-jvm</artifactId>
    <version>{version}</version>
</dependency>
```
