# 0.3.0

## Changes

`Kord#getGuilds()` has been replaced with the non-suspending `Kord#guilds`.
`@KordBuilder` has been renamed to `@KordDsl`.
`KordClientBuilder` has been renamed to `KordBuilder`.

# 0.2.0

## Additions

* Added the `core` module, a wrapper around `gateway` and `rest` that introduces caching using our `cache` module.
As with all our api, it's not stable yet and we will probably continue to introduce breaking changes to improve the 
general look and feel and fix design flaws.

* Along with `core` comes the concept of entity `Behaviour`, 
which are stripped down discord entities (mostly only retaining their id) that are able to interact with rest on a
higher level than the `rest` module exposes. This is specifically geared towards users who have disabled caching, but
its use should be preferred towards anyone who doesn't want to risk increased cache hits or rest calls.

* `Gateways` now come with a `detach` function, which will allow implementations to (permanently) shut down and release
all resources. `DefaultGateway` didn't require this, but other implementations that use e.g. thread pools can now shut
those down in here. 

* `DefaultGateway` now comes with a builder dsl that has sane defaults.

* `ExclusionHandler` now comes with a secondary constructor that accepts a token.

## Changes

* Kotlin's experimental `Duration` has replaced `Long` typed arguments that represented durations in milliseconds. The previous
functions/constructors have been marked with `@Deprecated` and should be replaced with the new ones.

## Fixes

* Numerous nullability fixes and other inconsistencies with/because of the discord api.

# 0.1.0

## Additions

* Added the `rest` module, a direct mapping of Discord's REST API with rate limiting
* Added a common module, containing shared code between rest and gateway 
* Preview and Experimental annotations

## Changes

* Moved shared json classes to common
* Removed Snowflake from common and gateway

## Fixes

* guildId field has been renamed to id in GuildIntegrations
* The color field in Embed is now optional
* The timestamp filed in Embed is now optional
* The All Permission now correctly represents all permissions
* Missing fields have been added to GuildIntegrations
* DefaultGateway should now correctly reconnect on reconnect events
* DefaultGateway should no longer delay on user invoked close
