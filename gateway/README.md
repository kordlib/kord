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
   implementation("com.gitlab.kordlib.kord:kord-gateway:{version}")
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
   implementation("com.gitlab.kordlib.kord:kord-gateway:{version}")
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
    <version>{version}</version>
</dependency>
```