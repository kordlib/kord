// THIS FILE IS AUTO-GENERATED BY KordEnumProcessor, DO NOT EDIT!
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
 * See [TeamMembershipState]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/teams#data-models-membership-state-enum).
 */
@Serializable(with = TeamMembershipState.Serializer::class)
@OptIn(KordUnsafe::class)
public sealed class TeamMembershipState(
    /**
     * The raw value used by Discord.
     */
    public val `value`: Int,
) {
    public final override fun equals(other: Any?): Boolean = this === other ||
            (other is TeamMembershipState && this.value == other.value)

    public final override fun hashCode(): Int = value.hashCode()

    public final override fun toString(): String =
            "TeamMembershipState.${this::class.simpleName}(value=$value)"

    /**
     * An unknown [TeamMembershipState].
     *
     * This is used as a fallback for [TeamMembershipState]s that haven't been added to Kord yet.
     */
    public class Unknown @KordUnsafe constructor(
        `value`: Int,
    ) : TeamMembershipState(value)

    public object Invited : TeamMembershipState(1)

    public object Accepted : TeamMembershipState(2)

    internal object Serializer : KSerializer<TeamMembershipState> {
        public override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.TeamMembershipState",
                PrimitiveKind.INT)

        public override fun serialize(encoder: Encoder, `value`: TeamMembershipState) =
                encoder.encodeInt(value.value)

        public override fun deserialize(decoder: Decoder) = when (val value = decoder.decodeInt()) {
            1 -> Invited
            2 -> Accepted
            else -> Unknown(value)
        }
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

    }
}
