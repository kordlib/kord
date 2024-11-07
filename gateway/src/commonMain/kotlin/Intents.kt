@file:Generate(
    BIT_SET_FLAGS, name = "Intent", valueName = "code",
    collectionHadCopy0 = true, hadBuilderFactoryFunction0 = true,
    kDoc = "Values that enable a group of events as defined by Discord.",
    docUrl = "https://discord.com/developers/docs/topics/gateway#gateway-intents",
    entries = [
        Entry(
            "Guilds", shift = 0,
            kDoc = """
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
                [here](https://discord.com/developers/docs/topics/gateway-events#thread-members-update))
            """,
        ),
        Entry(
            "GuildMembers", shift = 1, requiresOptInAnnotations = [privilegedIntentAnnotation],
            kDoc = """
                Enables the following events:
                - [GuildMemberAdd]
                - [GuildMemberUpdate]
                - [GuildMemberRemove]
                - [ThreadMembersUpdate] (contains different data depending on which intents are used, see
                [here](https://discord.com/developers/docs/topics/gateway-events#thread-members-update))
            """,
        ),
        Entry(
            "GuildModeration", shift = 2,
            kDoc = """
                Enables the following events:
                - [GuildAuditLogEntryCreate]
                - [GuildBanAdd]
                - [GuildBanRemove]
            """,
        ),
        Entry(
            "GuildExpressions", shift = 3,
            kDoc = """
                Enables the following events:
                - [GuildEmojisUpdate]
                - [GuildSoundboardSoundCreate]
                - [GuildSoundboardSoundUpdate]
                - [GuildSoundboardSoundsUpdate]
                - [GuildSoundboardSoundDelete]
            """
        ),
        Entry(
            "GuildEmojis", shift = 3,
            kDoc = """
                Enables the following events:
                - [GuildEmojisUpdate]
            """,
        ),
        Entry(
            "GuildIntegrations", shift = 4,
            kDoc = """
                Enables the following events:
                - [GuildIntegrationsUpdate]
                - [IntegrationCreate]
                - [IntegrationUpdate]
                - [IntegrationDelete]
            """,
        ),
        Entry(
            "GuildWebhooks", shift = 5,
            kDoc = """
                Enables the following events:
                - [WebhooksUpdate]
            """,
        ),
        Entry(
            "GuildInvites", shift = 6,
            kDoc = """
                Enables the following events:
                - [InviteCreate]
                - [InviteDelete]
            """,
        ),
        Entry(
            "GuildVoiceStates", shift = 7,
            kDoc = """
                Enables the following events:
                - [VoiceStateUpdate]
            """,
        ),
        Entry(
            "GuildPresences", shift = 8, requiresOptInAnnotations = [privilegedIntentAnnotation],
            kDoc = """
                Enables the following events:
                - [PresenceUpdate]
            """,
        ),
        Entry(
            "GuildMessages", shift = 9,
            kDoc = """
                Enables the following events:
                - [MessageCreate]
                - [MessageUpdate]
                - [MessageDelete]
                - [MessageDeleteBulk]
            """,
        ),
        Entry(
            "GuildMessageReactions", shift = 10,
            kDoc = """
                Enables the following events:
                - [MessageReactionAdd]
                - [MessageReactionRemove]
                - [MessageReactionRemoveAll]
                - [MessageReactionRemoveEmoji]
            """,
        ),
        Entry(
            "GuildMessageTyping", shift = 11,
            kDoc = """
                Enables the following events:
                - [TypingStart]
            """,
        ),
        Entry(
            "DirectMessages", shift = 12,
            kDoc = """
                Enables the following events:
                - [MessageCreate]
                - [MessageUpdate]
                - [MessageDelete]
                - [ChannelPinsUpdate]
            """,
        ),
        Entry(
            "DirectMessagesReactions", shift = 13,
            kDoc = """
                Enables the following events:
                - [MessageReactionAdd]
                - [MessageReactionRemove]
                - [MessageReactionRemoveAll]
                - [MessageReactionRemoveEmoji]
            """,
        ),
        Entry(
            "DirectMessageTyping", shift = 14,
            kDoc = """
                Enables the following events:
                - [TypingStart]
            """,
        ),
        Entry(
            "MessageContent", shift = 15, requiresOptInAnnotations = [privilegedIntentAnnotation],
            kDoc = "[MessageContent] is a unique [privileged intent][PrivilegedIntent] that isn't directly " +
                "associated with any Gateway [event][Event]s. Instead, access to [MessageContent] permits your app " +
                "to receive message content data across the APIs.\n\nFor example, the [content][dev.kord.common." +
                "entity.DiscordMessage.content], [embeds][dev.kord.common.entity.DiscordMessage.embeds], " +
                "[attachments][dev.kord.common.entity.DiscordMessage.attachments], and [components][dev.kord.common." +
                "entity.DiscordMessage.components] fields in [message objects][dev.kord.common.entity." +
                "DiscordMessage] all contain message content and therefore require this intent.\n\nApps **without** " +
                "this intent will receive empty values in fields that contain user-inputted content with a few " +
                "exceptions:\n" +
                "- Content in messages that an app sends\n" +
                "- Content in DMs with the app\n" +
                "- Content in which the app is mentioned\n" +
                "- Content of the message a message context menu command is used on",
        ),
        Entry(
            "GuildScheduledEvents", shift = 16,
            kDoc = """
                Enables the following events:
                - [GuildScheduledEventCreate]
                - [GuildScheduledEventUpdate]
                - [GuildScheduledEventDelete]
                - [GuildScheduledEventUserAdd]
                - [GuildScheduledEventUserRemove]
            """,
        ),
        Entry(
            "AutoModerationConfiguration", shift = 20,
            kDoc = """
                Enables the following events:
                - [AutoModerationRuleCreate]
                - [AutoModerationRuleUpdate]
                - [AutoModerationRuleDelete]
            """,
        ),
        Entry(
            "AutoModerationExecution", shift = 21,
            kDoc = """
                Enables the following events:
                - [AutoModerationActionExecution]
            """,
        ),
    ],
)

package dev.kord.gateway

import dev.kord.gateway.Intent.*
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.BIT_SET_FLAGS
import dev.kord.ksp.Generate.Entry
import kotlin.RequiresOptIn.Level.ERROR
import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.*

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
        "without enabling them. See https://discord.com/developers/docs/topics/gateway#privileged-intents for more " +
        "info on how to enable these.",
    level = ERROR,
)
@Retention(BINARY)
@Target(CLASS, PROPERTY, FUNCTION)
public annotation class PrivilegedIntent


@PrivilegedIntent
private val ALL_INTENTS = Intents(flags = Intent.entries)

@PrivilegedIntent
private val PRIVILEGED_INTENTS = Intents(GuildMembers, GuildPresences, MessageContent)

@OptIn(PrivilegedIntent::class)
private val NON_PRIVILEGED_INTENTS = Intents.ALL - Intents.PRIVILEGED

private val NO_INTENTS = Intents()


/**
 * All known [Intent]s (as contained in [Intent.entries]) combined into a single [Intents] instance.
 *
 * This is marked with [PrivilegedIntent] because it also contains all [privileged][PrivilegedIntent] [Intent]s.
 */
@PrivilegedIntent
public val Intents.Companion.ALL: Intents get() = ALL_INTENTS

/**
 * All known [privileged][PrivilegedIntent] [Intent]s combined into a single [Intents] instance.
 *
 * @see PrivilegedIntent
 */
@PrivilegedIntent
public val Intents.Companion.PRIVILEGED: Intents get() = PRIVILEGED_INTENTS

/**
 * All known non-[privileged][PrivilegedIntent] [Intent]s combined into a single [Intents] instance.
 *
 * @see PrivilegedIntent
 */
public val Intents.Companion.NON_PRIVILEGED: Intents get() = NON_PRIVILEGED_INTENTS

/** An [Intents] instance that contains no [Intent]s. */
public val Intents.Companion.NONE: Intents get() = NO_INTENTS
