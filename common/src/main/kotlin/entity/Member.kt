package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
public data class DiscordGuildMember(
    val user: Optional<DiscordUser> = Optional.Missing(),
    /*
    Don't trust the docs:
    2020-11-05 nick is only documented as nullable but can be missing through Gateway
    */
    val nick: Optional<String?> = Optional.Missing(),
    val roles: List<Snowflake>,
    @SerialName("joined_at")
    val joinedAt: Instant,
    @SerialName("premium_since")
    val premiumSince: Optional<Instant?> = Optional.Missing(),
    val deaf: OptionalBoolean = OptionalBoolean.Missing,
    val mute: OptionalBoolean = OptionalBoolean.Missing,
    val flags: GuildMemberFlags,
    val pending: OptionalBoolean = OptionalBoolean.Missing,
    val avatar: Optional<String?> = Optional.Missing(),
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
)


@Serializable
public data class DiscordInteractionGuildMember(
    val user: Optional<DiscordUser> = Optional.Missing(),
    /*
    Don't trust the docs:
    2020-11-05 nick is only documented as nullable but can be missing through Gateway
    */
    val nick: Optional<String?> = Optional.Missing(),
    val roles: List<Snowflake>,
    @SerialName("joined_at")
    val joinedAt: Instant,
    @SerialName("premium_since")
    val premiumSince: Optional<Instant?> = Optional.Missing(),
    val permissions: Permissions,
    val pending: OptionalBoolean = OptionalBoolean.Missing,
    val avatar: Optional<String?> = Optional.Missing(),
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
)


@Serializable
public data class DiscordAddedGuildMember(
    val user: Optional<DiscordUser> = Optional.Missing(),
    /*
    Don't trust the docs:
    2020-11-05 nick is only documented as nullable but can be missing through Gateway
    */
    val nick: Optional<String?> = Optional.Missing(),
    val roles: List<Snowflake>,
    @SerialName("joined_at")
    val joinedAt: Instant,
    @SerialName("premium_since")
    val premiumSince: Optional<Instant?> = Optional.Missing(),
    val deaf: Boolean,
    val mute: Boolean,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val pending: OptionalBoolean = OptionalBoolean.Missing,
    val avatar: Optional<String?> = Optional.Missing(),
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
)

@Serializable
public data class DiscordRemovedGuildMember(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val user: DiscordUser
)

@Serializable
public data class DiscordUpdatedGuildMember(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val roles: List<Snowflake>,
    val user: DiscordUser,
    val nick: Optional<String?> = Optional.Missing(),
    @SerialName("joined_at")
    val joinedAt: Instant,
    @SerialName("premium_since")
    val premiumSince: Optional<Instant?> = Optional.Missing(),
    val pending: OptionalBoolean = OptionalBoolean.Missing,
    val avatar: Optional<String?> = Optional.Missing(),
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
)

@Serializable
public data class DiscordThreadMember(
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("user_id")
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("join_timestamp")
    val joinTimestamp: Instant,
    val flags: Int
)

@Serializable(with = GuildMemberFlags.Companion::class)
public data class GuildMemberFlags(val code: Int) {

    public operator fun contains(flag: GuildMemberFlags): Boolean {
        return this.code and flag.code == flag.code
    }

    public companion object : KSerializer<GuildMemberFlags> {

        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("flags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): GuildMemberFlags {
            return GuildMemberFlags(decoder.decodeInt())
        }

        override fun serialize(encoder: Encoder, value: GuildMemberFlags) {
            encoder.encodeInt(value.code)
        }
    }

}

@Serializable
public enum class GuildMemberFlag(public val code: Int) {
    DidRejoin(1.shl(0)),
    CompletedOnboarding(1.shl(1)),
    BypassesVerification(1.shl(2)),
    StartedOnboarding(1.shl(3)),
}
