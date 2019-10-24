# Kord gateway

A low-level implementation of discord's [gateway](https://discordapp.com/developers/docs/topics/gateway).

## Example usage

```kotlin
suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val gateway = DefaultGateway { //optional builder showing the defaults
        url = "wss://gateway.discord.gg/"
        client = HttpClient(CIO) {
            install(WebSockets)
            install(JsonFeature)
        }

        retry = LinearRetry(2.seconds, 20.seconds, 10)
        rateLimiter = BucketRateLimiter(120, Duration.ofSeconds(60).toKotlinDuration())
    }

    gateway.events.filterIsInstance<MessageCreate>().flowOn(Dispatchers.IO).onEach {
        val words = it.message.content.split(' ')
        when (words.firstOrNull()) {
            "!close" -> gateway.stop()
            "!restart" -> gateway.restart()
            "!detach" -> gateway.detach()
            "!status" -> when (words.getOrNull(1)) {
                "playing" -> gateway.send(UpdateStatus(status = Status.Online, afk = false, game = Activity("Kord", ActivityType.Game)))
            }
        }
    }.launchIn(GlobalScope)

    gateway.start(token)
}
```

## Installation

### Gradle

```groovy
repositories {
 jcenter()
 maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
 implementation 'com.gitlab.kordlib:kord:gateway:0.2.2'
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
    <artifactId>gateway</artifactId>
    <version>0.2.2</version>
</dependency>
```