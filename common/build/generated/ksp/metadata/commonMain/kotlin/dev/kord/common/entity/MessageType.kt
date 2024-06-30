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
 * See [MessageType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#message-object-message-types).
 */
@Serializable(with = MessageType.Serializer::class)
public sealed class MessageType(
    /**
     * The raw code used by Discord.
     */
    public val code: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is MessageType && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()

    final override fun toString(): String = if (this is Unknown) "MessageType.Unknown(code=$code)"
            else "MessageType.${this::class.simpleName}"

    /**
     * An unknown [MessageType].
     *
     * This is used as a fallback for [MessageType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        code: Int,
    ) : MessageType(code)

    public object Default : MessageType(0)

    public object RecipientAdd : MessageType(1)

    public object RecipientRemove : MessageType(2)

    public object Call : MessageType(3)

    public object ChannelNameChange : MessageType(4)

    public object ChannelIconChange : MessageType(5)

    public object ChannelPinnedMessage : MessageType(6)

    public object UserJoin : MessageType(7)

    public object GuildBoost : MessageType(8)

    public object GuildBoostTier1 : MessageType(9)

    public object GuildBoostTier2 : MessageType(10)

    public object GuildBoostTier3 : MessageType(11)

    public object ChannelFollowAdd : MessageType(12)

    public object GuildDiscoveryDisqualified : MessageType(14)

    public object GuildDiscoveryRequalified : MessageType(15)

    public object GuildDiscoveryGracePeriodInitialWarning : MessageType(16)

    public object GuildDiscoveryGracePeriodFinalWarning : MessageType(17)

    public object ThreadCreated : MessageType(18)

    public object Reply : MessageType(19)

    public object ChatInputCommand : MessageType(20)

    public object ThreadStarterMessage : MessageType(21)

    public object GuildInviteReminder : MessageType(22)

    public object ContextMenuCommand : MessageType(23)

    public object AutoModerationAction : MessageType(24)

    public object RoleSubscriptionPurchase : MessageType(25)

    public object InteractionPremiumUpsell : MessageType(26)

    public object StageStart : MessageType(27)

    public object StageEnd : MessageType(28)

    public object StageSpeaker : MessageType(29)

    public object StageTopic : MessageType(31)

    public object GuildApplicationPremiumSubscription : MessageType(32)

    internal object Serializer : KSerializer<MessageType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.MessageType", PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: MessageType) {
            encoder.encodeInt(value.code)
        }

        override fun deserialize(decoder: Decoder): MessageType = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [MessageType]s.
         */
        public val entries: List<MessageType> by lazy(mode = PUBLICATION) {
            listOf(
                Default,
                RecipientAdd,
                RecipientRemove,
                Call,
                ChannelNameChange,
                ChannelIconChange,
                ChannelPinnedMessage,
                UserJoin,
                GuildBoost,
                GuildBoostTier1,
                GuildBoostTier2,
                GuildBoostTier3,
                ChannelFollowAdd,
                GuildDiscoveryDisqualified,
                GuildDiscoveryRequalified,
                GuildDiscoveryGracePeriodInitialWarning,
                GuildDiscoveryGracePeriodFinalWarning,
                ThreadCreated,
                Reply,
                ChatInputCommand,
                ThreadStarterMessage,
                GuildInviteReminder,
                ContextMenuCommand,
                AutoModerationAction,
                RoleSubscriptionPurchase,
                InteractionPremiumUpsell,
                StageStart,
                StageEnd,
                StageSpeaker,
                StageTopic,
                GuildApplicationPremiumSubscription,
            )
        }

        /**
         * Returns an instance of [MessageType] with [MessageType.code] equal to the specified
         * [code].
         */
        public fun from(code: Int): MessageType = when (code) {
            0 -> Default
            1 -> RecipientAdd
            2 -> RecipientRemove
            3 -> Call
            4 -> ChannelNameChange
            5 -> ChannelIconChange
            6 -> ChannelPinnedMessage
            7 -> UserJoin
            8 -> GuildBoost
            9 -> GuildBoostTier1
            10 -> GuildBoostTier2
            11 -> GuildBoostTier3
            12 -> ChannelFollowAdd
            14 -> GuildDiscoveryDisqualified
            15 -> GuildDiscoveryRequalified
            16 -> GuildDiscoveryGracePeriodInitialWarning
            17 -> GuildDiscoveryGracePeriodFinalWarning
            18 -> ThreadCreated
            19 -> Reply
            20 -> ChatInputCommand
            21 -> ThreadStarterMessage
            22 -> GuildInviteReminder
            23 -> ContextMenuCommand
            24 -> AutoModerationAction
            25 -> RoleSubscriptionPurchase
            26 -> InteractionPremiumUpsell
            27 -> StageStart
            28 -> StageEnd
            29 -> StageSpeaker
            31 -> StageTopic
            32 -> GuildApplicationPremiumSubscription
            else -> Unknown(code)
        }
    }
}
