@file:GenerateKordEnum(
    name = "Intent",
    valueType = GenerateKordEnum.ValueType.BITSET,
    isFlags = true,
    kDoc = "Values that enable a group of events as [defined by Discord](https://discord.com/developers/docs/topics/gateway#gateway-intents).",
    additionalImports = ["dev.kord.common.entity.DiscordMessage"],
    bitFlagsDescriptor = GenerateKordEnum.BitFlagDescription("gateway", "intents"),
    entries = [
        Entry(
            "Guilds", longValue = 1 shl 0, kDoc = """
         Enables the following events:
         - [GuildCreate]
         - [GuildUpdate]
         - [GuildDelete]
         - [GuildRoleCreate]
         - [GuildRoleUpdate]
         - [GuildRoleDelete]
         - [ChannelCreate]
         - [ChannelUpdate]
         - [ChannelDelete]
         - [ChannelPinsUpdate]
         - [ThreadCreate]
         - [ThreadUpdate]
         - [ThreadDelete]
         - [ThreadListSync]
         - [ThreadMemberUpdate]
         - [ThreadMembersUpdate] (contains different data depending on which intents are used, see
         [here](https://discord.com/developers/docs/topics/gateway#thread-members-update))
         """
        ),
        Entry(
            "GuildMembers", longValue = 1 shl 1, additionalOptInMarkerAnnotations = [privilegedIntentAnnotation], kDoc = """
         Enables the following events:
         - [GuildMemberAdd]
         - [GuildMemberUpdate]
         - [GuildMemberRemove]
         - [ThreadMembersUpdate] (contains different data depending on which intents are used, see
         [here](https://discord.com/developers/docs/topics/gateway#thread-members-update))
         """
        ),
        Entry(
            "GuildBans", longValue = 1 shl 2, kDoc = """
         Enables the following events:
         - [GuildBanAdd]
         - [GuildBanRemove]
         - [GuildAuditLogEntryCreate]
         """
        ),
        Entry(
            "GuildEmojis", longValue = 1 shl 3, kDoc = """
         Enables the following events:
         - [GuildEmojisUpdate]
         """
        ),
        Entry(
            "GuildIntegrations", longValue = 1 shl 4, kDoc = """
         Enables the following events:
         - [GuildIntegrationsUpdate]
         """
        ),
        Entry(
            "GuildWebhooks", longValue = 1 shl 5, kDoc = """
         Enables the following events:
         - [WebhooksUpdate]
         """
        ),
        Entry(
            "GuildInvites", longValue = 1 shl 6, kDoc = """
         Enables the following events:
         - [InviteCreate]
         - [InviteDelete]
         """
        ),

        Entry(
            "GuildVoiceStates", longValue = 1 shl 7, kDoc = """
            Enables the following events:
            - [VoiceStateUpdate]
            """
        ),
        Entry(
            "GuildPresences", longValue = 1 shl 8, additionalOptInMarkerAnnotations = [privilegedIntentAnnotation], kDoc = """
         Enables the following events:
         - [PresenceUpdate]
         """
        ),
        Entry(
            "GuildMessages", longValue = 1 shl 9, kDoc = """
         Enables the following events:
         - [MessageCreate]
         - [MessageUpdate]
         - [MessageDelete]
         - [MessageDeleteBulk]
         """
        ),
        Entry(
            "GuildMessageReactions", longValue = 1 shl 10, kDoc = """
         Enables the following events:
         - [MessageReactionAdd]
         - [MessageReactionRemove]
         - [MessageReactionRemoveAll]
         - [MessageReactionRemoveEmoji]
         """
        ),
        Entry(
            "GuildMessageTyping", longValue = 1 shl 11, kDoc = """
         Enables the following events:
         - [TypingStart]
         """
        ),
        Entry(
            "DirectMessages", longValue = 1 shl 12, kDoc = """
         Enables the following events:
         - [MessageCreate]
         - [MessageUpdate]
         - [MessageDelete]
         - [ChannelPinsUpdate]
         """
        ),
        Entry(
            "DirectMessagesReactions", longValue = 1 shl 13, kDoc = """
         Enables the following events:
         - [MessageReactionAdd]
         - [MessageReactionRemove]
         - [MessageReactionRemoveAll]
         - [MessageReactionRemoveEmoji]
         """
        ),
        Entry(
            "DirectMessageTyping", longValue = 1 shl 14, kDoc = """
         Enables the following events:
         - [TypingStart]
         """
        ),
        Entry(
            "MessageContent", longValue = 1 shl 15, additionalOptInMarkerAnnotations = [privilegedIntentAnnotation], kDoc = """
         [MessageContent] is a unique [privileged intent][PrivilegedIntent] that isn't directly associated with any
         Gateway [event][Event]s. Instead, access to [MessageContent] permits your app to receive message content data
         across the APIs.
         
         For example, the [content][DiscordMessage.content], [embeds][DiscordMessage.embeds],
         [attachments][DiscordMessage.attachments], and [components][DiscordMessage.components] fields in
         [message objects][DiscordMessage] all contain message content and therefore require this intent.
         
         Apps **without** this intent will receive empty values in fields that contain user-inputted content with a few
         exceptions:
         - content in messages that an app sends
         - content in DMs with the app
         - content in which the app is mentioned
         """
        ),
        Entry(
            "GuildScheduledEvents", longValue = 1 shl 16, kDoc = """
         Enables the following events:
         - [GuildScheduledEventCreate]
         - [GuildScheduledEventUpdate]
         - [GuildScheduledEventDelete]
         - [GuildScheduledEventUserAdd]
         - [GuildScheduledEventUserRemove]
         """
        ),
        Entry(
            "AutoModerationConfiguration", longValue = 1 shl 20, kDoc = """
         Enables the following events:
         - [AutoModerationRuleCreate]
         - [AutoModerationRuleUpdate]
         - [AutoModerationRuleDelete]
         """
        ),
        Entry(
            "AutoModerationExecution", longValue = 1 shl 21, kDoc = """
         Enables the following events:
         - [AutoModerationActionExecution]
         """
        ),
    ]
)

package dev.kord.gateway

import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import kotlin.RequiresOptIn.Level.ERROR
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

private const val privilegedIntentAnnotation = "dev.kord.gateway.PrivilegedIntent"

/**
Some [Intent]s are defined as "privileged" due to the sensitive nature of the data and cannot be used by Kord without
enabling them.
 *
Currently, those intents include:
- [Intent.GuildMembers]
- [Intent.GuildPresences]
- [Intent.MessageContent]
 *
See [the official documentation](https://discord.com/developers/docs/topics/gateway#privileged-intents) for more info
on how to enable these.
 */
@MustBeDocumented
@RequiresOptIn(
    """Some intents are defined as "privileged" due to the sensitive nature of the data and cannot be used by Kord """ +
            "without enabling them. See https://discord.com/developers/docs/topics/gateway#privileged-intents for " +
            "more info on how to enable these.",
    level = ERROR,
)
@Retention(RUNTIME)
@Target(CLASS, PROPERTY, FUNCTION)
public annotation class PrivilegedIntent

/**
 * All non intents.
 *
 * @see PrivilegedIntent
 */
@PrivilegedIntent
public val Intents.Companion.all: Intents
    get() = Intents(Intent.entries)

/**
 * All privileged intents.
 *
 * @see PrivilegedIntent
 */
@PrivilegedIntent
public val Intents.Companion.privileged: Intents
    get() = Intents(Intent.GuildPresences, Intent.GuildMembers, Intent.MessageContent)

/**
 * All non privileged intents.
 *
 * @see PrivilegedIntent
 */
@OptIn(PrivilegedIntent::class)
public val Intents.Companion.nonPrivileged: Intents
    get() = Intents {
        +all
        -privileged
    }

/**
 * No intents.
 */
public val Intents.Companion.none: Intents get() = Intents()
