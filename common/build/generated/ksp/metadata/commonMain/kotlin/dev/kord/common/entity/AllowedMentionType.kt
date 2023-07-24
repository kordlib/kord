// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

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
 * See [AllowedMentionType]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/resources/channel#allowed-mentions-object-allowed-mention-types).
 */
@Serializable(with = AllowedMentionType.Serializer::class)
public sealed class AllowedMentionType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is AllowedMentionType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            "AllowedMentionType.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [AllowedMentionType].
     *
     * This is used as a fallback for [AllowedMentionType]s that haven't been added to Kord yet.
     */
    public class Unknown(
        `value`: String,
    ) : AllowedMentionType(value)

    /**
     * Controls role mentions.
     */
    public object RoleMentions : AllowedMentionType("roles")

    /**
     * Controls user mentions
     */
    public object UserMentions : AllowedMentionType("users")

    /**
     * Controls @everyone and @here mentions.
     */
    public object EveryoneMentions : AllowedMentionType("everyone")

    internal object Serializer : KSerializer<AllowedMentionType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.AllowedMentionType",
                PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: AllowedMentionType) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): AllowedMentionType =
                when (val value = decoder.decodeString()) {
            "roles" -> RoleMentions
            "users" -> UserMentions
            "everyone" -> EveryoneMentions
            else -> Unknown(value)
        }
    }

    public companion object {
        /**
         * A [List] of all known [AllowedMentionType]s.
         */
        public val entries: List<AllowedMentionType> by lazy(mode = PUBLICATION) {
            listOf(
                RoleMentions,
                UserMentions,
                EveryoneMentions,
            )
        }

    }
}
