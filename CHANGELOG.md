# 0.5.1

## Fixes

* `DiscordInvite#targetUser` is now correctly nullable.

# 0.5.0

## Additions

* `mention` properties for `GuildEmoji` and `ReactionEmoji`

# 0.5.0-rc2

## Additions

* Added [Gateway Intents](https://github.com/discordapp/discord-api-docs/blob/feature/gateway-intents/docs/topics/Gateway.md#gateway-intents), allowing users to filter events send by Discord. #60
* Added `VIEW_GUILD_INSIGHTS` permission #88
* Added `MessageCreateEvent#guildId` to replace the removed `Message#guildId`.
* Added `MessageCreateEvent#member`.

## Removals

* Removed deprecated API from 0.4.x

## Dependencies

* kotlin 1.3.70 -> 1.3.72
* ktor 1.3.1 -> 1.3.2
* kotlinx.coroutines 1.3.4 -> 1.3.7
* kotlin-logging 1.7.8 -> 1.7.10

# 0.5.0-rc1

## Performance

* Getting entities from a flow of non-cached entities should be considerably faster.

## Fixes

* non-final socket closures use 4900 instead of 1000.

## Additions

* Added the `EntitySupplyStrategy`, entities will now keep a reference to 
a `EntitySupplier` (fetched from the strategy) from which they'll be able to fetch other entities (`getX` methods). #74
* Added `withStrategy(EntitySupplyStrategy)` to change the `EntitySupplier` of most entities. #74
* Added a `CacheEntitySupplier` and `RestEntitySupplier` supplier that exclusively operates on Cache and REST
respectively, as well as a strategy that prioritizes Cache over REST like previous versions. #74
* Added `getXOrNull` variants to fetching entities that won't explode when trying to get an entity that doesn't
exist. #74

## Changes

* Changed `GuildCreateBuilder` to be more inline with recent api changes. #77
* `core` Event constructors are no longer internal.
* `Kord#gateway` is now a `MasterGateway`, exposing all sharded gateways #65
* `core` Events now expose their shard index as well as the `Gateway` they were spawned from. #65
* `DisconnectEvent` and `Close` have been extended with more detailed implementations #65
* `Gateway` now accepts a `PresenceBuilder` to configure its original presence. #72
* Rest Discord API version can now be configured by setting the `com.gitlab.kordlib.rest.version` system property, `v6` by default.
* `DefaultGateway` now supports zlib compression and enables it by default.

# 0.4.22

This release contains breaking changes related to webhooks.

## Additions

* Added `WebhookBehavior#executeIgnored` which does not wait for the message to be processed.

## Changes

* `WebhookBehavior#execute` will now return a `Message`.

## Fixes

* Fixed an issue when deserializing a Webhook would throw an exception.
* Fixed an issue where endpoints returning nullable types would throw an exception.

# 0.4.21

## Fixes

* Fixed cache not removing messages deleted from a `MessageDeleteBulk`.

# 0.4.20

## Additions

* Added error codes 10026(Unknown ban), 40002(You need to verify your account in order to perform this action) and
30015(Maximum number of attachments in a message reached (10)) to the `JsonErrorCode`.
* Added `approximate_member_count` `approximate_presence_count` to guilds, which will be present when requesting a guild
through rest.
* Added missing `DmChannel#recipientBehaviors`.
* Added missing `MessageUpdateEvent#message`, `MessageUpdateEvent#channel`, `MessageUpdateEvent#getMessage`,
`MessageUpdateEvent#getChannel`.
* Added missing `PresenceUpdateEvent#member`, `PresenceUpdateEvent#guild`, `PresenceUpdateEvent#getUser`,
`PresenceUpdateEvent#getMember`, `PresenceUpdateEvent#member`, `PresenceUpdateEvent#getGuild`.
* Added `chunk_index` and `chunck_count` properties to `GuildMembersChunkData`.
* Added `Invite#targetUserType`.
* Added `User#flag`.
* Added `GuildFeature.WelcomeScreenEnabled`.
* Added `MessageModifyBuilder#allowedMentions`.
* `Embeds` can now copy their contents over to builders by using `Embed#apply(EmbedBuilder)`.
* Added `User#premiumType`.
* Added `GuildPreview` and the ability to get previews for public guilds via `Kord#getGuildPreview`.

## Fixes

* Fixed `MessageModifyBuilder` ignoring flags.


# 0.4.19

## Fixes

* Fixed an issue where `ClientStatus` would only display the desktop status.

# 0.4.18

## Fixes

* Fixed an issue where presences from guild creates were cached without guild id.

# 0.4.17

## Fixes

* Fixed REST throwing an exception when parsing an error without code.

# 0.4.16

## Changes
* Behaviors have been removed from VoiceState due to lack of guildId.

## Fixes
* Unmatched data structure between VoiceState and its data.
* ISO_INSTANT not being used to format and Instant object.

# 0.4.15

## Changes
* Classes implementing `Entity` now correctly implement equals and hashcode based on ids.

# 0.4.14

## Fixes
* Fix `GuildMessageChannelBehavior#bulkDelete` manually deleting messages younger than 14 days and trying to bulk delete messages 
older than 14 days, instead of the other way around. 

# 0.4.13

## Fixes
* Fix guild emojis not having their correct id.

# 0.4.12

## Fixes
* Fix unexpected data fields throwing exceptions when parsing `Gateway` `Events`. This should now be limited to
unknown opcodes only.

# 0.4.11

## Fixes
* `ParallelRequestRateLimiter` will no longer try to unlock a mutex twice on a error response

# 0.4.10
## Fixes
* `DiscordErrorResponse` incorrect serialization

# 0.4.9

ID collections in modify builders have undergone a breaking critical bug fix.
## Additions
* Added `JsonErrorCode` and `DiscordErrorResponse` to map Discord's Json error messages.

## Changes
* Added `error` field to `KtorRequestException` to include `DiscordErrorResponse`   
## Fixes

* Fixed `permissionOverwrites` in `TextChannelModifyBuilder`, `VoiceChannelModifyBuilder`, `NewsChannelModifyBuilder` being final.
* Fixed `roles` in `EmojiModifyBuilder` and `MemberModifyBuilder` being final.

# 0.4.8

## Additions

* Added Integrations.
* Guilds can now request their own Integrations with `GuildBhehavior#integrations`.
* Added `preferredLocale` and `publicUpdatesChannelId` to `Guild` and `GuildModifyBuilder`.
* Added some utility functions to the `KordCacheBuilder`.
* Added `GuildDiscoveryDisqualified` and `GuildDiscoveryRequalified` to `MessageType`. #79

## Changes

* `KtorRequestHandler` will now log the body of requests and responses.

## Fixes

* The GuildService now returns the correct type of integration objects.
* Fixed a typo in the `Embed#type` property kdocs.
* Fixed an issue where paginated flows would emit duplicate items.

## Deprecations

* `Embed#type` has been deprecated. #80
* `LiveNewsChannel`, `LiveStoreChannel` and `LiveTextChannel` have been deprecated. Message channels in guilds can 
change type during their lifetime, which means type can't be guaranteed. `LiveGuildMessageChannel` has been introduced as
an alternative.

# 0.4.7

## Fixes

* Fixed an issue where editing a guild category would reset permissions.
* Fixed an issue where editing a guild emoji would reset permissions.
* Fixed an issue where editing a guild member would reset permissions.

## Removals

* Removed some `Kord` functions that fetched non 'top-level' entities. Kord is no longer
the central source for getting entities and these changes try to reflect that. #74
You should use the new entity suppliers for those instead. #74
* `KordCache` has been removed, Kord now keeps a reference to a generic cache instead.
You can still get similar behavior using `kord.with(EntitySupplyStrategy.cache)`. #74


# 0.4.6

## Additions

* Added `Guild#memberCount`.
* Added `GuildEmoji#isAvaiable`. #84
* Added allowed mentions to message create. #83


## Changes

* `MessageChannel#withTyping` should now properly stop when cancelling the coroutine context

## Fixes

* Fixed channels cached in guild create events not having a guild id.
* Fixed `DiscordCreatedInvite#maxUses` serialization typo.
* Fixed an issue where editing a guild channel would reset permissions.

# 0.4.5

## Additions

* Added `Member#roleBehaviors`
* You can now add or remove entire `Permissions` to/from the `PermissionsBuilder`
* Added `Member#isOwner`
* Added `Member#getPermissions`
* Added `Message.isPinned`
* Added `GuildChannel#getEffectivePermissions`

## Changes

* `Message#guildId` and `Message#guild` are deprecated due to inconsistent availability.
* removed `ReactionEmoji.id` due to compiler issues regarding nullable inline classes, check for Custom type instead.

## Fixes

* Guild emojis update correctly on `GuildEmojisUpdate` event.
* Ratelimiters should no longer lock up when throwing an exception during requests.

# 0.4.4

## Additions

* Added `MessageBehavior#withTyping`.

## Changes

* usage of `kotlinx.io.inputstream` has been replaced with `java.io.inputstream` following the internalization
of the typealias.

## Dependencies

* kotlin 1.3.61 -> 1.3.70
* kotlinx.serialization 0.14.0 -> 0.20.0
* kotlinx.coroutines 1.3.3 -> 1.3.4

# 0.4.3

## Additions

* Added `Guild#roleBehaviors`.

## Fixes

* Fix incorrect deserialization of mentioned roles in messages.
* Message updates now correctly update mentioned channels.

# 0.4.2

## Additions

* Added `GuildBehavior.getRole`.
* Added missing `Guild.channelIds` and `Guild.channelBehaviors`.

# 0.4.1

## Additions

* Added a `CategoryCreateBuilder` to `GuildBehavior`. #67

## Fixes

* Removed `url` from `EmbedFooterRequest`, as it's not an actual field in the Discord API.  #66

# 0.4.0

## Additions

* `Kord` and its`Cache` now implement a common interface `EntitySupplier` to retrieve entities that can be cached.  #54
* `mentionedRoleIds`, `mentionedRoleBehaviors`, `mentionedUserIds`, `mentionedUserBehaviors` were added to `Message`.
* Introduced two implementations for the `RequestRateLimiter`: `ExclusionRequestRateLimiter` and `ParallelRequestRateLimiter`, 
which will be replacing the `ExclusionRequestHandler` and `ParallelRequestHandler` respectively. #59

## Changes

* All fields but `name` are now optional in `GuildCreateBuilder` and `GuildCreateRequest`. #62
* Removed the `ExclusionRequestHandler` and `ParallelRequestHandler` and introduced the `KtorRequestHandler`, which accepts any `RequestRateLimiter`. #59
* `Message#mentionedRoles` and `Message#mentionedUsers` now return a `Flow` of their respective entities instead of a `Set<Snwoflake>`.
* `StoreChannel#edit`, `TextChannel#edit` and `NewsChannel#edit` now supply their builder as a receiver.
* core entity builders were moved from `com.gitlab.kordlib.core.builder` to `com.gitlab.kordlib.rest.builder` and are now
part of the rest module.
* `Snowflake` was moved to the common module from core. #53
* `Kord#getRegions()` was deprecated for `Kord#regions`.
* `Kord#getUsers()` was deprecated for `cache#users`.
* various Snowflake argument names have been changed in`Kord` to better reflect the entity they represent.
* A reified `getChannel` has been added to `EntitySupplier` that will try to cast the channel to the given type.
* `CategoryModifyBuilder#permissions` is now a `val`.
* `ChannelPermissionModifyBuilder` is now a proper `AuditRequestBuilder`.

## Fixes

* fixed typo `CategoryModifyBuilder#positon` -> `CategoryModifyBuilder#position`

# 0.3.3

## Fixes

* Fixed `guild-id` being wrongly deserialized as `guildId` in `DiscordAddedGuildMember`.

* Fixed an issue where disconnecting from the `DefaultGateway` 
without closing the connection (i.e. dropping your internet connection) would indefinitely suspend the `DefaultGateway`,
making it unusable.

# 0.3.2

## Additions

* Added `InviteCreate`, `InviteDelete` an `MessageReactionRemoveEmoji` events. #61
* Added `deleteAllReactionsForEmoji` to ChannelService. #61

## Fixes 

* Fixed an issue where `DiscordInvite` was wrongly representing `inviter` as a `String` instead of a `DiscordUser`.

## Dependencies

* ktor 1.3.0-rc2 -> 1.3.0

# 0.3.1

## Fixes 

* Fixed an issue with Kord ignoring cached entries that relied on querying data by id. *again*.

# 0.3.0

> This version contains an upgrade of ktor that brings breaking changes, be sure to check out ktor's changelog if you
> were interacting with ktor or the rest module directly.
> 
> This change also comes with the removal of kotlinx.io, which was a transitive dependency. If your code relied on kotlinx.io
> consider manually including the dependency or migrating away from it entirely.

## Changes

* `Kord#getGuilds()` has been replaced with the non-suspending `Kord#guilds`.
* `@KordBuilder` has been renamed to `@KordDsl`.
* `KordClientBuilder` has been renamed to `KordBuilder`.
* `fileName` has been renamed to `filename`.
* `DefaultGateway`'s constructor accepts a single `DefaultGatewayData` instead of multiple properties.
* `DefaultGateway` is now able to rate limit identify attempts and accepts a `RateLimiter` that can be shared between multiple gateways. 
This will be done by default for Kord clients now.
* `KordClientBuilder`'s `gateway` function has been renamed to `gateways`. It now gives a list of shards and requests a list of gateways, this change
allows you to more easily share configuration between gateways.
* Emojis now have nullable names, this only appears when interacting with guild emojis that have been deleted.
* Rest, Gateway and Common entities have gained a `Discord` prefix to reduce name collisions with Core.
* `ParallalRequestHandler` has been upgraded to stable.
* `features` in `Guilds` are now represented as enum values instead of Strings. 

## Additions

* Added `LiveEntity` and its implementations to Core. These are self-updating entities that contain a filtered
event stream, only emitting related events.
* Added `targetUser` and `targetUserType` to invite creation. #47
* Added a `rules channel`, `SystemChannelFlags` and `discoverySplash` to `Guild`. #48
* Added `premium since` to `Member`. #45
* Added `inviter` to `Invite`. #44

## Fixes

* Ending the process while enabling the shutdownHook and without logging in causes UninitializedPropertyAccessException #50

## Dependencies 

* gradle 5.4 -> 6.0.1
* kotlin -> 1.3.60 -> 1.3.61
* kotlinx.serialization 0.13.0 -> 0.14.0
* ktor 1.2.5 -> 1.3.0-rc2
* kotlinx.coroutines 1.3.2 -> 1.3.3
* kotlin-logging 1.7.6 -> 1.7.8

# 0.2.4

## Additions

* `uses` has been added as a nullable property to `InviteResponse` .
* Added `mentionedChannel`-related fields to `Message`.
* Added `KordClientBuilder#enableShutdownHook`, which enables a shutdownHook that automatically closes the gateway on process exit.

## Changes

* `User.Avatar#getUrl` no longer returns `null` when requesting a static version of a user's animated avatar.

## Fixes

* Fixed an issue where `PartialEmoji` would not deserialize with a missing `id`.
* Fixed an issue where subscribing to the `events` Flow while kord was already logged in caused it to emit old events.
* Fixed an issue where `DefaultGateway` would stop reading payloads after throwing an Exception while parsing json.

# 0.2.3

## Additions

Enums now have an `Unknown` value to mitigate the effects of unannounced discord changes moving forward. [#39](https://gitlab.com/kordlib/kord/issues/39)
* `nicknameMention` has been added to `MemberBehavior`

## Changes

* `Flow` extension now support suspending functions

## Fixes

* `MessageCreateBuilder#addFile` no longer ignores files added.
* `GuildMembersChunkData#presences` has become nullable.
* `RequestGuildMembers#query` is no longer nullable.
* Fixed an issue with Kord ignoring cached entries that relied on querying data by id.
* `User#discriminator` and `User#tag` will now properly format discriminators with leading spaces.

## Dependencies
* kotlin-logging: 1.7.6
* kotlinx.coroutines: 1.3.2
* kotlinx.serialization 0.13.0
* ktor 1.2.5

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

# 0.2.2

## Additions

`Gateway` now has a `ping` field, containing the duration between the latest heartbeat and heartbeat ack.

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

# 0.2.1

This is the first maintenance update for Kord 0.2. 
With it, we have started hosting Kord on bintray, check our README on what to include to get the newest version.

## Additions

* Added `Flow<T: Any>.firstOrNull` and `Flow<T: Any>.any` as their behavior is often needed when interacting with
flows of members, channels, etc (and really, they should've been part of the coroutines api).

## Changes

* `KordClientBuilder` now allows you to pass a custom `CoroutineDispatcher`.
* Since most suspending calls in Kord will be IO related, `Kord` now uses `Dispatchers.IO` as its `CoroutineDispatcher`.
* `StoreChannel` can no longer be used to read or send messages. [discord api](https://discordapp.com/developers/docs/resources/channel#channel-object-example-store-channel).
* `NewsChannel` and `StoreChannel` have been upgraded to the stable api and are no longer in preview.

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
