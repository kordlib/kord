@file:JvmName("IntentUtil")

@file:Generate(
    BIT_SET_FLAGS, name = "Intent",
    kDoc = "Values that enable a group of events as [defined by Discord](https://discord.com/developers/docs/topics/gateway#gateway-intents).",
    docUrl = "https://discord.com/developers/docs/topics/gateway#gateway-intents",
    bitFlagsDescriptor = BitFlagDescription("gateway", "intents", "an", "intent"),
    entries = [
        Entry(
            "Guilds", shift = 0, kDoc = """
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
            "GuildMembers",
            shift = 1,
            additionalOptInMarkerAnnotations = [privilegedIntentAnnotation],
            kDoc = """
         Enables the following events:
         - [GuildMemberAdd]
         - [GuildMemberUpdate]
         - [GuildMemberRemove]
         - [ThreadMembersUpdate] (contains different data depending on which intents are used, see
         [here](https://discord.com/developers/docs/topics/gateway#thread-members-update))
         """
        ),
        Entry(
            "GuildBans", shift = 2, kDoc = """
         Enables the following events:
         - [GuildAuditLogEntryCreate]
         - [GuildBanAdd]
         - [GuildBanRemove]
         """
        ),
        Entry(
            "GuildEmojis", shift = 3, kDoc = """
         Enables the following events:
         - [GuildEmojisUpdate]
         """
        ),
        Entry(
            "GuildIntegrations", shift = 4, kDoc = """
         Enables the following events:
         - [GuildIntegrationsUpdate]
         - [IntegrationCreate]
         - [IntegrationUpdate]
         - [IntegrationDelete]
         """
        ),
        Entry(
            "GuildWebhooks", shift = 5, kDoc = """
         Enables the following events:
         - [WebhooksUpdate]
         """
        ),
        Entry(
            "GuildInvites", shift = 6, kDoc = """
         Enables the following events:
         - [InviteCreate]
         - [InviteDelete]
         """
        ),

        Entry(
            "GuildVoiceStates", shift = 7, kDoc = """
            Enables the following events:
            - [VoiceStateUpdate]
            """
        ),
        Entry(
            "GuildPresences",
            shift = 8,
            additionalOptInMarkerAnnotations = [privilegedIntentAnnotation],
            kDoc = """
         Enables the following events:
         - [PresenceUpdate]
         """
        ),
        Entry(
            "GuildMessages", shift = 9, kDoc = """
         Enables the following events:
         - [MessageCreate]
         - [MessageUpdate]
         - [MessageDelete]
         - [MessageDeleteBulk]
         """
        ),
        Entry(
            "GuildMessageReactions", shift = 10, kDoc = """
         Enables the following events:
         - [MessageReactionAdd]
         - [MessageReactionRemove]
         - [MessageReactionRemoveAll]
         - [MessageReactionRemoveEmoji]
         """
        ),
        Entry(
            "GuildMessageTyping", shift = 11, kDoc = """
         Enables the following events:
         - [TypingStart]
         """
        ),
        Entry(
            "DirectMessages", shift = 12, kDoc = """
         Enables the following events:
         - [MessageCreate]
         - [MessageUpdate]
         - [MessageDelete]
         - [ChannelPinsUpdate]
         """
        ),
        Entry(
            "DirectMessagesReactions", shift = 13, kDoc = """
         Enables the following events:
         - [MessageReactionAdd]
         - [MessageReactionRemove]
         - [MessageReactionRemoveAll]
         - [MessageReactionRemoveEmoji]
         """
        ),
        Entry(
            "DirectMessageTyping", shift = 14, kDoc = """
         Enables the following events:
         - [TypingStart]
         """
        ),
        Entry(
            "MessageContent",
            shift = 15,
            additionalOptInMarkerAnnotations = [privilegedIntentAnnotation],
            kDoc = """
         [MessageContent] is a unique [privileged intent][PrivilegedIntent] that isn't directly associated with any
         Gateway [event][Event]s. Instead, access to [MessageContent] permits your app to receive message content data
         across the APIs.
         
         For example, the [content][dev.kord.common.entity.DiscordMessage.content],
         [embeds][dev.kord.common.entity.DiscordMessage.embeds],
         [attachments][dev.kord.common.entity.DiscordMessage.attachments],
         and [components][dev.kord.common.entity.DiscordMessage.components] fields in
         [message objects][dev.kord.common.entity.DiscordMessage] all contain message content and therefore require this
         intent.
         
         Apps **without** this intent will receive empty values in fields that contain user-inputted content with a few
         exceptions:
         - Content in messages that an app sends
         - Content in DMs with the app
         - Content in which the app is mentioned
         - Content of the message a message context menu command is used on
         """
        ),
        Entry(
            "GuildScheduledEvents", shift = 16, kDoc = """
         Enables the following events:
         - [GuildScheduledEventCreate]
         - [GuildScheduledEventUpdate]
         - [GuildScheduledEventDelete]
         - [GuildScheduledEventUserAdd]
         - [GuildScheduledEventUserRemove]
         """
        ),
        Entry(
            "AutoModerationConfiguration", shift = 20, kDoc = """
         Enables the following events:
         - [AutoModerationRuleCreate]
         - [AutoModerationRuleUpdate]
         - [AutoModerationRuleDelete]
         """
        ),
        Entry(
            "AutoModerationExecution", shift = 21, kDoc = """
         Enables the following events:
         - [AutoModerationActionExecution]
         """
        ),
    ]
)

package dev.kord.gateway

import dev.kord.gateway.Intent.*
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.BitFlagDescription
import dev.kord.ksp.Generate.EntityType.BIT_SET_FLAGS
import dev.kord.ksp.Generate.Entry
import kotlin.RequiresOptIn.Level.ERROR
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.jvm.JvmName

private const val privilegedIntentAnnotation = "dev.kord.gateway.PrivilegedIntent"

/**
 * Some [Intent]s are defined as "privileged" due to the sensitive nature of the data and cannot be used by Kord without
 * enabling them.
 *
 * Currently, those intents include:
 * - [GuildMembers]
 * - [GuildPresences]
 * - [MessageContent]
 *
 * See [the official documentation](https://discord.com/developers/docs/topics/gateway#privileged-intents) for more info
 * on how to enable these.
 */
@MustBeDocumented
@RequiresOptIn(
    "Some intents are defined as \"privileged\" due to the sensitive nature of the data and cannot be used by Kord " +
            "without enabling them. See https://discord.com/developers/docs/topics/gateway#privileged-intents for " +
            "more info on how to enable these.",
    level = ERROR,
)
@Retention(BINARY)
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
    get() = Intents(GuildPresences, GuildMembers, MessageContent)

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
