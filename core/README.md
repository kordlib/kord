# Kord Core

Kord `core` is an implementation of the discord api build on top of the [gateway](https://gitlab.com/kordlib/kord/tree/master/gateway), 
[rest](https://gitlab.com/kordlib/kord/tree/master/rest) and [cache](https://gitlab.com/kordlib/cache) modules. It features a high level representation of Discord's entities and their behaviour
in a non-blocking, coroutine focused, event-driven design.

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

Replace `{version}` with the latest version number on maven central.

For Snapshots replace `{version}` with `{branch}-SNAPSHOT`

e.g: `0.7.x-SNAPSHOT`

[![Download](https://img.shields.io/nexus/r/dev.kord/kord-core?color=fb5502&label=Kord&logoColor=05c1fd&server=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2F&style=for-the-badge) ](https://search.maven.org/search?q=g:dev.kord)
[![Snapshot](https://img.shields.io/nexus/s/dev.kord/kord-core?label=SNAPSHOT&server=https%3A%2F%2Foss.sonatype.org%2F&style=for-the-badge)](https://oss.sonatype.org/#nexus-search;quick~dev.kord)
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
