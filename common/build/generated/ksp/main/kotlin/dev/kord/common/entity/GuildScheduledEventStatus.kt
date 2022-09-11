// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor.kt, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.ReplaceWith
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmField
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = GuildScheduledEventStatus.NewSerializer::class)
public sealed class GuildScheduledEventStatus(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is GuildScheduledEventStatus && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
            "GuildScheduledEventStatus.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [GuildScheduledEventStatus].
     *
     * This is used as a fallback for [GuildScheduledEventStatus]s that haven't been added to Kord
     * yet.
     */
    public class Unknown(
        `value`: Int,
    ) : GuildScheduledEventStatus(value)

    public object Scheduled : GuildScheduledEventStatus(1)

    public object Active : GuildScheduledEventStatus(2)

    public object Completed : GuildScheduledEventStatus(3)

    public object Cancelled : GuildScheduledEventStatus(4)

    internal object NewSerializer : KSerializer<GuildScheduledEventStatus> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildScheduledEventStatus",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: GuildScheduledEventStatus) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> Scheduled
            2 -> Active
            3 -> Completed
            4 -> Cancelled
            else -> Unknown(value)
        }
    }

    @Deprecated(
        message = "Use 'GuildScheduledEventStatus.serializer()' instead.",
        replaceWith = ReplaceWith(expression = "GuildScheduledEventStatus.serializer()", imports =
                    arrayOf("dev.kord.common.entity.GuildScheduledEventStatus")),
    )
    public object Serializer : KSerializer<GuildScheduledEventStatus> by NewSerializer {
        @Deprecated(
            message = "Use 'GuildScheduledEventStatus.serializer()' instead.",
            replaceWith = ReplaceWith(expression = "GuildScheduledEventStatus.serializer()", imports
                        = arrayOf("dev.kord.common.entity.GuildScheduledEventStatus")),
        )
        public fun serializer(): KSerializer<GuildScheduledEventStatus> = this
    }

    public companion object {
        /**
         * A [List] of all known [GuildScheduledEventStatus]s.
         */
        public val entries: List<GuildScheduledEventStatus> by lazy(mode = PUBLICATION) {
            listOf(
                Scheduled,
                Active,
                Completed,
                Cancelled,
            )
        }


        @Suppress(names = arrayOf("DEPRECATION"))
        @Deprecated(
            level = DeprecationLevel.HIDDEN,
            message = "Binary compatibility",
        )
        @JvmField
        public val Serializer: Serializer = Serializer
    }
}
