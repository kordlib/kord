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

### Gradle

```groovy
repositories {
 jcenter()
 maven { url 'https://jitpack.io' }
}
```

```groovy
dependencies {
 implementation 'com.gitlab.kordlib:kord:rest:0.2.3'
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
    <version>0.2.3</version>
</dependency>
```