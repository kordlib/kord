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

Replace `{version}` with the latest version number on maven central.

For Snapshots replace `{version}` with `{branch}-SNAPSHOT`

e.g: `0.7.x-SNAPSHOT`

[![Download](https://img.shields.io/nexus/r/dev.kord/kord-gateway?color=fb5502&label=Kord&logoColor=05c1fd&server=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2F&style=for-the-badge) ](https://search.maven.org/search?q=g:dev.kord)
[![Snapshot](https://img.shields.io/nexus/s/dev.kord/kord-gateway?label=SNAPSHOT&server=https%3A%2F%2Foss.sonatype.org%2F&style=for-the-badge)](https://oss.sonatype.org/#nexus-search;quick~dev.kord)

### Gradle (groovy)

```groovy
repositories {
    mavenCentral()
    // Kord Snapshots Repository (Optional):
    maven("https://oss.sonatype.org/content/repositories/snapshots")

}
```

```groovy
dependencies {
    implementation("dev.kord:kord-gateway:{version}")
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
    implementation("dev.kord:kord-gateway:{version}")
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
    <artifactId>kord-gateway</artifactId>
    <version>{version}</version>
</dependency>
```
