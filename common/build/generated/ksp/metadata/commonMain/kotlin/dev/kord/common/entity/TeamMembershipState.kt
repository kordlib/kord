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
 * See [TeamMembershipState]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/teams#data-models-membership-state-enum).
 */
@Serializable(with = TeamMembershipState.Serializer::class)
public sealed class TeamMembershipState(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is TeamMembershipState && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "TeamMembershipState.Unknown(value=$value)"
            else "TeamMembershipState.${this::class.simpleName}"

    /**
     * An unknown [TeamMembershipState].
     *
     * This is used as a fallback for [TeamMembershipState]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: Int,
    ) : TeamMembershipState(value)

    public object Invited : TeamMembershipState(1)

    public object Accepted : TeamMembershipState(2)

    internal object Serializer : KSerializer<TeamMembershipState> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.TeamMembershipState",
                PrimitiveKind.INT)

        override fun serialize(encoder: Encoder, `value`: TeamMembershipState) {
            encoder.encodeInt(value.value)
        }

        override fun deserialize(decoder: Decoder): TeamMembershipState = from(decoder.decodeInt())
    }

    public companion object {
        /**
         * A [List] of all known [TeamMembershipState]s.
         */
        public val entries: List<TeamMembershipState> by lazy(mode = PUBLICATION) {
            listOf(
                Invited,
                Accepted,
            )
        }


        /**
         * Returns an instance of [TeamMembershipState] with [TeamMembershipState.value] equal to
         * the specified [value].
         */
        public fun from(`value`: Int): TeamMembershipState = when (value) {
            1 -> Invited
            2 -> Accepted
            else -> Unknown(value)
        }
    }
}
