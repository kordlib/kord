// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordPreview
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 *
 *
 * See [GuildJoinRequestStatus]es in the [Discord Developer Documentation]().
 */
@Serializable(with = GuildJoinRequestStatus.Serializer::class)
@KordPreview
public sealed class GuildJoinRequestStatus(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is GuildJoinRequestStatus && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "GuildJoinRequestStatus.Unknown(value=$value)" else "GuildJoinRequestStatus.${this::class.simpleName}"

    /**
     * An unknown [GuildJoinRequestStatus].
     *
     * This is used as a fallback for [GuildJoinRequestStatus]es that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : GuildJoinRequestStatus(value)

    /**
     * The request is started but not submitted
     */
    public object Started : GuildJoinRequestStatus("STARTED")

    /**
     * The request has been submitted
     */
    public object Submitted : GuildJoinRequestStatus("SUBMITTED")

    /**
     * The request has been rejected
     */
    public object Rejected : GuildJoinRequestStatus("REJECTED")

    /**
     * The request has been approved
     */
    public object Approved : GuildJoinRequestStatus("APPROVED")

    internal object Serializer : KSerializer<GuildJoinRequestStatus> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.GuildJoinRequestStatus", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: GuildJoinRequestStatus) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): GuildJoinRequestStatus = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [GuildJoinRequestStatus]es.
         */
        public val entries: List<GuildJoinRequestStatus> by lazy(mode = PUBLICATION) {
            listOf(
                Started,
                Submitted,
                Rejected,
                Approved,
            )
        }

        /**
         * Returns an instance of [GuildJoinRequestStatus] with [GuildJoinRequestStatus.value] equal to the specified [value].
         */
        public fun from(`value`: String): GuildJoinRequestStatus = when (value) {
            "STARTED" -> Started
            "SUBMITTED" -> Submitted
            "REJECTED" -> Rejected
            "APPROVED" -> Approved
            else -> Unknown(value)
        }
    }
}
