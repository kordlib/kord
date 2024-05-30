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
 * See [TeamMemberRole]s in the
 * [Discord Developer Documentation](https://discord.com/developers/docs/topics/teams#team-member-roles).
 */
@Serializable(with = TeamMemberRole.Serializer::class)
public sealed class TeamMemberRole(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other ||
            (other is TeamMemberRole && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String =
            if (this is Unknown) "TeamMemberRole.Unknown(value=$value)"
            else "TeamMemberRole.${this::class.simpleName}"

    /**
     * An unknown [TeamMemberRole].
     *
     * This is used as a fallback for [TeamMemberRole]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : TeamMemberRole(value)

    /**
     * Admins have similar access as owners, except they cannot take destructive actions on the team
     * or team-owned apps.
     */
    public object Admin : TeamMemberRole("admin")

    /**
     * Developers can access information about team-owned apps, like the client secret or public
     * key. They can also take limited actions on team-owned apps, like configuring interaction
     * endpoints or resetting the bot token. Members with the Developer role _cannot_ manage the team
     * or its members, or take destructive actions on team-owned apps.
     */
    public object Developer : TeamMemberRole("developer")

    /**
     * Read-only members can access information about a team and any team-owned apps. Some examples
     * include getting the IDs of applications and exporting payout records.
     */
    public object ReadOnly : TeamMemberRole("read_only")

    internal object Serializer : KSerializer<TeamMemberRole> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.kord.common.entity.TeamMemberRole",
                PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: TeamMemberRole) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): TeamMemberRole = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [TeamMemberRole]s.
         */
        public val entries: List<TeamMemberRole> by lazy(mode = PUBLICATION) {
            listOf(
                Admin,
                Developer,
                ReadOnly,
            )
        }


        /**
         * Returns an instance of [TeamMemberRole] with [TeamMemberRole.value] equal to the
         * specified [value].
         */
        public fun from(`value`: String): TeamMemberRole = when (value) {
            "admin" -> Admin
            "developer" -> Developer
            "read_only" -> ReadOnly
            else -> Unknown(value)
        }
    }
}
