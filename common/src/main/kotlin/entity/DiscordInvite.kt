package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
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
public data class DiscordInvite(
    val code: String,
    val guild: Optional<DiscordPartialGuild> = Optional.Missing(),
    val channel: DiscordChannel?,
    val inviter: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("target_user")
    val targetUser: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("target_user_type")
    val targetUserType: Optional<TargetUserType> = Optional.Missing(),
    @SerialName("approximate_presence_count")
    val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
    @SerialName("approximate_member_count")
    val approximateMemberCount: OptionalInt = OptionalInt.Missing,
    @SerialName("expires_at")
    val expiresAt: Optional<Instant?> = Optional.Missing(),
    @SerialName("stage_instance")
    val stageInstance: Optional<DiscordStageInstance> = Optional.Missing(),
    @SerialName("guild_scheduled_event")
    val guildScheduledEvent: Optional<DiscordGuildScheduledEvent> = Optional.Missing(),
)

@Serializable
public data class DiscordPartialInvite(
    /*
    Do not trust the docs:
    2020-11-06 This is actually never null, the endpoint(Get Guild Vanity URL) returns
    a HTTP 4xx instead when the guild has no vanity url.
     */
    val code: String?,
    val uses: Int
)

@Serializable
public data class DiscordInviteMetadata(
    val uses: Int,
    @SerialName("max_uses")
    val maxUses: Int,
    @SerialName("max_age")
    val maxAge: Int,
    val temporary: Boolean,
    @SerialName("created_at")
    val createdAt: String,
)

@Serializable(with = InviteTargetType.Serializer::class)
public sealed class InviteTargetType(public val value: Int) {
    public class Unknown(value: Int) : InviteTargetType(value)
    public object Stream : InviteTargetType(1)
    public object EmbeddedApplication : InviteTargetType(2)

    internal object Serializer : KSerializer<InviteTargetType> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("InviteTargetType", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): InviteTargetType = when (val value = decoder.decodeInt()) {
            1 -> Stream
            2 -> EmbeddedApplication
            else -> Unknown(value)
        }

        override fun serialize(encoder: Encoder, value: InviteTargetType) {
            encoder.encodeInt(value.value)
        }
    }
}
