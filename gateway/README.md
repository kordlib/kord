# Kord gateway

A low-level implementation of discord's [gateway](https://discord.com/developers/docs/topics/gateway).

## Example usage

```kotlin
suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val gateway = DefaultGateway { // optional builder for custom configuration
        client = HttpClient {
            install(WebSockets)
        }
        reconnectRetry = LinearRetry(2.seconds, 20.seconds, 10)
        sendRateLimiter = IntervalRateLimiter(120, 60.seconds)
        dispatcher = Dispatchers.Default
    }

    gateway.events.filterIsInstance<MessageCreate>().onEach {
        val words = it.message.content.split(' ')
        when (words.firstOrNull()) {
            "!close" -> gateway.stop()
            "!detach" -> gateway.detach()
            "!status" -> when (words.getOrNull(1)) {
                "playing" -> gateway.editPresence {
                    status = PresenceStatus.Online
                    afk = false
                    playing("Kord")
                }
            }
        }
    }.launchIn(gateway)

    gateway.start(token) {
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
    implementation("dev.kord:kord-gateway:{version}")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation("dev.kord:kord-gateway:{version}")
}
```

### Maven

```xml
<dependency>
    <groupId>dev.kord</groupId>
    <artifactId>kord-gateway-jvm</artifactId>
    <version>{version}</version>
</dependency>
```
