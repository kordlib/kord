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

Replace `{version}` with the latest version number on maven central.

For Snapshots replace `{version}` with `{branch}-SNAPSHOT`

e.g: `0.7.x-SNAPSHOT`

[![Download](https://img.shields.io/nexus/r/dev.kord/kord-rest?color=fb5502&label=Kord&logoColor=05c1fd&server=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2F&style=for-the-badge) ](https://search.maven.org/search?q=g:dev.kord)
[![Snapshot](https://img.shields.io/nexus/s/dev.kord/kord-rest?label=SNAPSHOT&server=https%3A%2F%2Foss.sonatype.org%2F&style=for-the-badge)](https://oss.sonatype.org/#nexus-search;quick~dev.kord)


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
    implementation("dev.kord:kord-rest:{version}")
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
    implementation("dev.kord:kord-rest:{version}")
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
    <artifactId>kord-rest</artifactId>
    <version>{version}</version>
</dependency>
```
