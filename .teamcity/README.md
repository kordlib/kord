# .teamcity

This folder houses the [TeamCity](https://jetbrains.com/teamcity) configurations for the Kord build process

# Documentation

Documentation can be
found [here](https://www.jetbrains.com/help/teamcity/kotlin-dsl.html) ([KDoc](https://kord.teamcity.com/app/dsl-documentation/index.html))

# Importing in IntelliJ

File -> Project Structure -> Modules -> Add -> Import ->
Select [pom.xml](pom.xml) -> Select Maven

# Validating

To Validate the config format run the following Maven goal

```shell
mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate
```

# Builds

| Name               | Description                              | Definition                                         |
|--------------------|------------------------------------------|----------------------------------------------------|
| Validation         | Runs checks (Test, binary compatibility) | [ValidationCI.kt](ValidationCI.kt)                 |
| Docs               | Deploys documentation to GitHub Pages    | [DocsCI.kt](DocsCI.kt)                             |
| GraalVM Validation | Runs GraalVM Native image Tests          | [GraalVMNativeImageCI.kt](GraalVMNativeImageCI.kt) |
