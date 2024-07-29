# Kord REST Client

A low-level implementation of discord's rest api.

## Example usage

```kotlin
suspend fun main(args: Array<String>) {
    val token = args.firstOrNull() ?: error("token required")

    val rest = RestClient(KtorRequestHandler(token))

    val username = rest.user.getCurrentUser().username
    println("using $username's token")
}
```

## Installation

See the root [README](../README.md#installation) for more information.

### Gradle (Kotlin)

```kotlin
dependencies {
    implementation("dev.kord:kord-rest:{version}")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation("dev.kord:kord-rest:{version}")
}
```

### Maven

```xml
<dependency>
    <groupId>dev.kord</groupId>
    <artifactId>kord-rest-jvm</artifactId>
    <version>{version}</version>
</dependency>
```
