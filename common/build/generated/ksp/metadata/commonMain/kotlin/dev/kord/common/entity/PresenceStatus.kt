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
 * See [PresenceStatus]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/gateway-events#update-presence-status-types).
 */
@Serializable(with = PresenceStatus.Serializer::class)
public sealed class PresenceStatus(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is PresenceStatus && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "PresenceStatus.Unknown(value=$value)"
            else "PresenceStatus.${this::class.simpleName}"

    /**
     * An unknown [PresenceStatus].
     *
     * This is used as a fallback for [PresenceStatus]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : PresenceStatus(value)

    /**
     * Online.
     */
    public object Online : PresenceStatus("online")

    /**
     * Do Not Disturb.
     */
    public object DoNotDisturb : PresenceStatus("dnd")

    /**
     * AFK.
     */
    public object Idle : PresenceStatus("idle")

    /**
     * Invisible and shown as offline.
     */
    public object Invisible : PresenceStatus("invisible")

    /**
     * Offline.
     */
    public object Offline : PresenceStatus("offline")

    internal object Serializer : KSerializer<PresenceStatus> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.PresenceStatus",
                PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: PresenceStatus) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): PresenceStatus = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [PresenceStatus]s.
         */
        public val entries: List<PresenceStatus> by lazy(mode = PUBLICATION) {
            listOf(
                Online,
                DoNotDisturb,
                Idle,
                Invisible,
                Offline,
            )
        }

        /**
         * Returns an instance of [PresenceStatus] with [PresenceStatus.value] equal to the
         * specified [value].
         */
        public fun from(`value`: String): PresenceStatus = when (value) {
            "online" -> Online
            "dnd" -> DoNotDisturb
            "idle" -> Idle
            "invisible" -> Invisible
            "offline" -> Offline
            else -> Unknown(value)
        }
    }
}
