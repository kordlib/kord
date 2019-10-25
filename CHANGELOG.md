# 0.2.3

## Fixes

`MessageCreateBuilder#addFile` no longer ignores files added.

# 0.2.2

## Additions

A new `RequestHandler`, the `ParallalRequestHandler` has been introduced as a preview feature. Compared to the 
`ExclusionRequestHandler`, this handler offers increased parallelism by allowing requests with different identifiers
to be handled in parallel. The drawback is that this opens a small window for exceeding the global rate limit.

`Gateway` now has a `ping` field, containing the duration between the latest heartbeat and heartbeat ack.
`GuildModifyRequest` now has an optional `banner` field, which can contain a `base64 16:9 png/jpeg image for the guild banner (when the server has BANNER feature)`.
Added `presences` and `userIds` to the `RequestGuildMembers` class and the equivalents to `GuildMembersChunkData`.

## Removals

`Invite#revoked` has been removed since it [never existed](https://github.com/discordapp/discord-api-docs/commit/70390b75377098204ccda75e3a7240a1604c7639).

## Fixes

`filename` is now correctly deserialized for `Attachment` objects.

# 0.2.1

This is the first maintenance update for Kord 0.2. 
With it, we have started hosting Kord on bintray, check our README on what to include to get the newest version.

## Additions

* Added `Flow<T: Any>.firstOrNull` and `Flow<T: Any>.any` as their behavior is often needed when interacting with
flows of members, channels, etc (and really, they should've been part of the coroutines api).
* Added `isSelfSteaming` to `VoiceState`, indicating when a user  is streaming using "Go Live".

## Changes

* `KordClientBuilder` now allows you to pass a custom `CoroutineDispatcher`.
* Since most suspending calls in Kord will be IO related, `Kord` now uses `Dispatchers.IO` as its `CoroutineDispatcher`.
* `StoreChannel` can no longer be used to read or send messages. [discord api](https://discordapp.com/developers/docs/resources/channel#channel-object-example-store-channel).
* `NewsChannel` and `StoreChannel` have been upgraded to the stable api and are no longer in preview.
* `ExclusionRequestHandler` now takes request buckets into consideration.

## Fixes

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
