// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * The type of action.
 */
@Serializable(with = AutoModerationActionType.Serializer::class)
public sealed class AutoModerationActionType(
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is AutoModerationActionType && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
            "AutoModerationActionType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [AutoModerationActionType].
     *
     * This is used as a fallback for [AutoModerationActionType]s that haven't been added to Kord
     * yet.
     */
    public class Unknown(
        `value`: Int,
    ) : AutoModerationActionType(value)

    /**
     * Blocks the content of a message according to the rule.
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
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.AutoModerationActionType",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: AutoModerationActionType) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> BlockMessage
            2 -> SendAlertMessage
            3 -> Timeout
            else -> Unknown(value)
        }
    }

    public companion object {
        public val entries: List<AutoModerationActionType> by lazy(mode = PUBLICATION) {
            listOf(
                BlockMessage,
                SendAlertMessage,
                Timeout,
            )
        }

    }
}
