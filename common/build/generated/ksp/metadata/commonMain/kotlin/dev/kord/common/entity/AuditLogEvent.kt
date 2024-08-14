// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * See [AuditLogEvent]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/audit-log#audit-log-entry-object-audit-log-events).
 */
@Serializable(with = AuditLogEvent.Serializer::class)
public sealed class AuditLogEvent(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is AuditLogEvent && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "AuditLogEvent.Unknown(value=$value)"
            else "AuditLogEvent.${this::class.simpleName}"

    /**
     * An unknown [AuditLogEvent].
     *
     * This is used as a fallback for [AuditLogEvent]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : AuditLogEvent(value)

    /**
     * Server settings were updated.
     */
    public object GuildUpdate : AuditLogEvent(1)

    /**
     * Channel was created.
     */
    public object ChannelCreate : AuditLogEvent(10)

    /**
     * Channel settings were updated.
     */
    public object ChannelUpdate : AuditLogEvent(11)

    /**
     * Channel was deleted.
     */
    public object ChannelDelete : AuditLogEvent(12)

    /**
     * Permission overwrite was added to a channel.
     */
    public object ChannelOverwriteCreate : AuditLogEvent(13)

    /**
     * Permission overwrite was updated for a channel.
     */
    public object ChannelOverwriteUpdate : AuditLogEvent(14)

    /**
     * Permission overwrite was deleted from a channel.
     */
    public object ChannelOverwriteDelete : AuditLogEvent(15)

    /**
     * Member was removed from server.
     */
    public object MemberKick : AuditLogEvent(20)

    /**
     * Members were pruned from server.
     */
    public object MemberPrune : AuditLogEvent(21)

    /**
     * Member was banned from server.
     */
    public object MemberBanAdd : AuditLogEvent(22)

    /**
     * Server ban was lifted for a member.
     */
    public object MemberBanRemove : AuditLogEvent(23)

    /**
     * Member was updated in server.
     */
    public object MemberUpdate : AuditLogEvent(24)

    /**
     * Member was added or removed from a role.
     */
    public object MemberRoleUpdate : AuditLogEvent(25)

    /**
     * Member was moved to a different voice channel.
     */
    public object MemberMove : AuditLogEvent(26)

    /**
     * Member was disconnected from a voice channel.
     */
    public object MemberDisconnect : AuditLogEvent(27)

    /**
     * Bot user was added to server.
     */
    public object BotAdd : AuditLogEvent(28)

    /**
     * Role was created.
     */
    public object RoleCreate : AuditLogEvent(30)

    /**
     * Role was edited.
     */
    public object RoleUpdate : AuditLogEvent(31)

    /**
     * Role was deleted.
     */
    public object RoleDelete : AuditLogEvent(32)

    /**
     * Server invite was created.
     */
    public object InviteCreate : AuditLogEvent(40)

    /**
     * Server invite was updated.
     */
    public object InviteUpdate : AuditLogEvent(41)

    /**
     * Server invite was deleted.
     */
    public object InviteDelete : AuditLogEvent(42)

    /**
     * Webhook was created.
     */
    public object WebhookCreate : AuditLogEvent(50)

    /**
     * Webhook properties or channel were updated.
     */
    public object WebhookUpdate : AuditLogEvent(51)

    /**
     * Webhook was deleted.
     */
    public object WebhookDelete : AuditLogEvent(52)

    /**
     * Emoji was created.
     */
    public object EmojiCreate : AuditLogEvent(60)

    /**
     * Emoji name was updated.
     */
    public object EmojiUpdate : AuditLogEvent(61)

    /**
     * Emoji was deleted.
     */
    public object EmojiDelete : AuditLogEvent(62)

    /**
     * Single message was deleted.
     */
    public object MessageDelete : AuditLogEvent(72)

    /**
     * Multiple messages were deleted.
     */
    public object MessageBulkDelete : AuditLogEvent(73)

    /**
     * Message was pinned to a channel.
     */
    public object MessagePin : AuditLogEvent(74)

    /**
     * Message was unpinned from a channel.
     */
    public object MessageUnpin : AuditLogEvent(75)

    /**
     * App was added to server.
     */
    public object IntegrationCreate : AuditLogEvent(80)

    /**
     * App was updated (as an example, its scopes were updated).
     */
    public object IntegrationUpdate : AuditLogEvent(81)

    /**
     * App was removed from server.
     */
    public object IntegrationDelete : AuditLogEvent(82)

    /**
     * Stage instance was created (stage channel becomes live).
     */
    public object StageInstanceCreate : AuditLogEvent(83)

    /**
     * Stage instance details were updated.
     */
    public object StageInstanceUpdate : AuditLogEvent(84)

    /**
     * Stage instance was deleted (stage channel no longer live).
     */
    public object StageInstanceDelete : AuditLogEvent(85)

    /**
     * Sticker was created.
     */
    public object StickerCreate : AuditLogEvent(90)

    /**
     * Sticker details were updated.
     */
    public object StickerUpdate : AuditLogEvent(91)

    /**
     * Sticker was deleted.
     */
    public object StickerDelete : AuditLogEvent(92)

    /**
     * Event was created.
     */
    public object GuildScheduledEventCreate : AuditLogEvent(100)

    /**
     * Event was updated.
     */
    public object GuildScheduledEventUpdate : AuditLogEvent(101)

    /**
     * Event was cancelled.
     */
    public object GuildScheduledEventDelete : AuditLogEvent(102)

    /**
     * Thread was created in a channel.
     */
    public object ThreadCreate : AuditLogEvent(110)

    /**
     * Thread was updated.
     */
    public object ThreadUpdate : AuditLogEvent(111)

    /**
     * Thread was deleted.
     */
    public object ThreadDelete : AuditLogEvent(112)

    /**
     * Permissions were updated for a command.
     */
    public object ApplicationCommandPermissionUpdate : AuditLogEvent(121)

    /**
     * Auto Moderation rule was created.
     */
    public object AutoModerationRuleCreate : AuditLogEvent(140)

    /**
     * Auto Moderation rule was updated.
     */
    public object AutoModerationRuleUpdate : AuditLogEvent(141)

    /**
     * Auto Moderation rule was deleted.
     */
    public object AutoModerationRuleDelete : AuditLogEvent(142)

    /**
     * Message was blocked by Auto Moderation.
     */
    public object AutoModerationBlockMessage : AuditLogEvent(143)

    /**
     * Message was flagged by Auto Moderation.
     */
    public object AutoModerationFlagToChannel : AuditLogEvent(144)

    /**
     * Member was timed out by Auto Moderation.
     */
    public object AutoModerationUserCommunicationDisabled : AuditLogEvent(145)

    /**
     * Creator monetization request was created.
     */
    public object CreatorMonetizationRequestCreated : AuditLogEvent(150)

    /**
     * Creator monetization terms were accepted.
     */
    public object CreatorMonetizationTermsAccepted : AuditLogEvent(151)

    internal object Serializer : KSerializer<AuditLogEvent> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.AuditLogEvent", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: AuditLogEvent) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): AuditLogEvent = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [AuditLogEvent]s.
         */
        public val entries: List<AuditLogEvent> by lazy(mode = PUBLICATION) {
            listOf(
                GuildUpdate,
                ChannelCreate,
                ChannelUpdate,
                ChannelDelete,
                ChannelOverwriteCreate,
                ChannelOverwriteUpdate,
                ChannelOverwriteDelete,
                MemberKick,
                MemberPrune,
                MemberBanAdd,
                MemberBanRemove,
                MemberUpdate,
                MemberRoleUpdate,
                MemberMove,
                MemberDisconnect,
                BotAdd,
                RoleCreate,
                RoleUpdate,
                RoleDelete,
                InviteCreate,
                InviteUpdate,
                InviteDelete,
                WebhookCreate,
                WebhookUpdate,
                WebhookDelete,
                EmojiCreate,
                EmojiUpdate,
                EmojiDelete,
                MessageDelete,
                MessageBulkDelete,
                MessagePin,
                MessageUnpin,
                IntegrationCreate,
                IntegrationUpdate,
                IntegrationDelete,
                StageInstanceCreate,
                StageInstanceUpdate,
                StageInstanceDelete,
                StickerCreate,
                StickerUpdate,
                StickerDelete,
                GuildScheduledEventCreate,
                GuildScheduledEventUpdate,
                GuildScheduledEventDelete,
                ThreadCreate,
                ThreadUpdate,
                ThreadDelete,
                ApplicationCommandPermissionUpdate,
                AutoModerationRuleCreate,
                AutoModerationRuleUpdate,
                AutoModerationRuleDelete,
                AutoModerationBlockMessage,
                AutoModerationFlagToChannel,
                AutoModerationUserCommunicationDisabled,
                CreatorMonetizationRequestCreated,
                CreatorMonetizationTermsAccepted,
            )
        }

        /**
         * Returns an instance of [AuditLogEvent] with [AuditLogEvent.value] equal to the specified
         * [value].
         */
        public fun from(`value`: Int): AuditLogEvent = when (value) {
            1 -> GuildUpdate
            10 -> ChannelCreate
            11 -> ChannelUpdate
            12 -> ChannelDelete
            13 -> ChannelOverwriteCreate
            14 -> ChannelOverwriteUpdate
            15 -> ChannelOverwriteDelete
            20 -> MemberKick
            21 -> MemberPrune
            22 -> MemberBanAdd
            23 -> MemberBanRemove
            24 -> MemberUpdate
            25 -> MemberRoleUpdate
            26 -> MemberMove
            27 -> MemberDisconnect
            28 -> BotAdd
            30 -> RoleCreate
            31 -> RoleUpdate
            32 -> RoleDelete
            40 -> InviteCreate
            41 -> InviteUpdate
            42 -> InviteDelete
            50 -> WebhookCreate
            51 -> WebhookUpdate
            52 -> WebhookDelete
            60 -> EmojiCreate
            61 -> EmojiUpdate
            62 -> EmojiDelete
            72 -> MessageDelete
            73 -> MessageBulkDelete
            74 -> MessagePin
            75 -> MessageUnpin
            80 -> IntegrationCreate
            81 -> IntegrationUpdate
            82 -> IntegrationDelete
            83 -> StageInstanceCreate
            84 -> StageInstanceUpdate
            85 -> StageInstanceDelete
            90 -> StickerCreate
            91 -> StickerUpdate
            92 -> StickerDelete
            100 -> GuildScheduledEventCreate
            101 -> GuildScheduledEventUpdate
            102 -> GuildScheduledEventDelete
            110 -> ThreadCreate
            111 -> ThreadUpdate
            112 -> ThreadDelete
            121 -> ApplicationCommandPermissionUpdate
            140 -> AutoModerationRuleCreate
            141 -> AutoModerationRuleUpdate
            142 -> AutoModerationRuleDelete
            143 -> AutoModerationBlockMessage
            144 -> AutoModerationFlagToChannel
            145 -> AutoModerationUserCommunicationDisabled
            150 -> CreatorMonetizationRequestCreated
            151 -> CreatorMonetizationTermsAccepted
            else -> Unknown(value)
        }
    }
}
