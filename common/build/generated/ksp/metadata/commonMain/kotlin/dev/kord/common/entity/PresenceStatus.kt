// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("RedundantVisibilityModifier", "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection",
                "RedundantUnitReturnType"))

package dev.kord.common.entity

import dev.kord.common.`annotation`.KordUnsafe
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
@OptIn(KordUnsafe::class)
public sealed class PresenceStatus(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is PresenceStatus && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
            "PresenceStatus.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [PresenceStatus].
     *
     * This is used as a fallback for [PresenceStatus]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
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
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.PresenceStatus",
                PrimitiveKind.STRING)

        public override fun serialize(encoder: Encoder, `value`: PresenceStatus) =
                encoder.encodeString(value.value)

        public override fun deserialize(decoder: Decoder) =
                when (val value = decoder.decodeString()) {
            "online" -> Online
            "dnd" -> DoNotDisturb
            "idle" -> Idle
            "invisible" -> Invisible
            "offline" -> Offline
            else -> Unknown(value)
        }
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

    }
}
