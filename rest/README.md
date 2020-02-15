# Kord gateway

A low-level implementation of discord's rest api.

## Example usage

```kotlin
suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val client = RestClient(ExclusionRequestHandler(token))

    val username = client.user.getCurrentUser().username
    println("using $username's token")
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
   implementation("com.gitlab.kordlib:kord-rest:{version}")
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
   implementation("com.gitlab.kordlib:kord:rest:{version}")
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
    <artifactId>rest</artifactId>
    <version>{version}</version>
</dependency>
```