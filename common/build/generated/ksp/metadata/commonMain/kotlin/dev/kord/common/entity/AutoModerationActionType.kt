// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection", "GrazieInspection", "MemberVisibilityCanBePrivate"))

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
 * The type of action.
 *
 * See [AutoModerationActionType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/auto-moderation#auto-moderation-action-object-action-types).
 */
@Serializable(with = AutoModerationActionType.Serializer::class)
public sealed class AutoModerationActionType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is AutoModerationActionType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "AutoModerationActionType.Unknown(value=$value)"
            else "AutoModerationActionType.${this::class.simpleName}"

    /**
     * An unknown [AutoModerationActionType].
     *
     * This is used as a fallback for [AutoModerationActionType]s that haven't been added to Kord
     * yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : AutoModerationActionType(value)

    /**
     * Blocks a member's message and prevents it from being posted.
     *
     * A custom explanation can be specified and shown to members whenever their message is blocked.
     */
    public object BlockMessage : AutoModerationActionType(1)

    /**
     * Logs user content to a specified channel.
     */
    public object SendAlertMessage : AutoModerationActionType(2)

    /**
     * Timeout user for a specified duration.
     *
     * A [Timeout] action can only be set up for
     * [Keyword][dev.kord.common.entity.AutoModerationRuleTriggerType.Keyword] and
     * [MentionSpam][dev.kord.common.entity.AutoModerationRuleTriggerType.MentionSpam] rules. The
     * [ModerateMembers][dev.kord.common.entity.Permission.ModerateMembers] permission is required to
     * use the [Timeout] action type.
     */
    public object Timeout : AutoModerationActionType(3)

    internal object Serializer : KSerializer<AutoModerationActionType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationActionType",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: AutoModerationActionType) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): AutoModerationActionType =
                from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [AutoModerationActionType]s.
         */
        public val entries: List<AutoModerationActionType> by lazy(mode = PUBLICATION) {
            listOf(
                BlockMessage,
                SendAlertMessage,
                Timeout,
            )
        }


        /**
         * Returns an instance of [AutoModerationActionType] with [AutoModerationActionType.value]
         * equal to the specified [value].
         */
        public fun from(`value`: Int): AutoModerationActionType = when (value) {
            1 -> BlockMessage
            2 -> SendAlertMessage
            3 -> Timeout
            else -> Unknown(value)
        }
    }
}
