package dev.kord.rest.json.request

import dev.kord.common.Color
import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DefaultMessageNotificationLevel
import dev.kord.common.entity.DiscordWelcomeScreenChannel
import dev.kord.common.entity.ExplicitContentFilter
import dev.kord.common.entity.IntegrationExpireBehavior
import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.ScheduledEntityType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StageInstancePrivacyLevel
import dev.kord.common.entity.VerificationLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class GuildCreateRequest(
    val name: String,
    val region: Optional<String> = Optional.Missing(),
    val icon: Optional<String> = Optional.Missing(),
    @SerialName("verification_level")
    val verificationLevel: Optional<VerificationLevel> = Optional.Missing(),
    @SerialName("default_message_notifications")
    val defaultNotificationLevel: Optional<DefaultMessageNotificationLevel> = Optional.Missing(),
    val explicitContentFilter: Optional<ExplicitContentFilter> = Optional.Missing(),
    val roles: Optional<List<GuildRoleCreateRequest>> = Optional.Missing(),
    val channels: Optional<List<GuildChannelCreateRequest>> = Optional.Missing(),
    @SerialName("afk_channel_id")
    val afkChannelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("afk_timeout")
    val afkTimeout: OptionalInt = OptionalInt.Missing,
    @SerialName("system_channel_id")
    val systemChannelId: OptionalSnowflake = OptionalSnowflake.Missing
)

@Serializable
data class GuildChannelCreateRequest(
    val name: String,
    val type: ChannelType,
    val topic: Optional<String> = Optional.Missing(),
    val bitrate: OptionalInt = OptionalInt.Missing,
    @SerialName("user_limit")
    val userLimit: OptionalInt = OptionalInt.Missing,
    @SerialName("rate_limit_per_user")
    val rateLimitPerUser: Optional<Int> = Optional.Missing(),
    val position: OptionalInt = OptionalInt.Missing,
    @SerialName("permission_overwrites")
    val permissionOverwrite: Optional<List<Overwrite>> = Optional.Missing(),
    @SerialName("parent_id")
    val parentId: OptionalSnowflake = OptionalSnowflake.Missing,
    val nsfw: OptionalBoolean = OptionalBoolean.Missing,
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
)

@Serializable
data class ChannelPositionSwapRequest(
    val id: Snowflake,
    val position: OptionalInt? = OptionalInt.Missing,
    @KordExperimental
    @SerialName("lock_permissions")
    val lockPermissions: Boolean?,
    @KordExperimental
    @SerialName("parent_id")
    val parentId: Snowflake?
)

@Serializable(with = GuildChannelPositionModifyRequest.Serializer::class)
data class GuildChannelPositionModifyRequest(
    val swaps: List<ChannelPositionSwapRequest>
) {
    internal object Serializer : KSerializer<GuildChannelPositionModifyRequest> {
        private val delegate = ListSerializer(ChannelPositionSwapRequest.serializer())

        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor
            get() = listSerialDescriptor(ChannelPositionSwapRequest.serializer().descriptor)

        override fun serialize(encoder: Encoder, value: GuildChannelPositionModifyRequest) {
            delegate.serialize(encoder, value.swaps)
        }

        override fun deserialize(decoder: Decoder): GuildChannelPositionModifyRequest {
            return GuildChannelPositionModifyRequest(decoder.decodeSerializableValue(delegate))
        }

    }
}

@Serializable
data class GuildMemberAddRequest(
    @SerialName("access_token")
    val token: String,
    val nick: Optional<String> = Optional.Missing(),
    val roles: Optional<Set<Snowflake>> = Optional.Missing(),
    val mute: OptionalBoolean = OptionalBoolean.Missing,
    val deaf: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable
data class GuildMemberModifyRequest(
    val nick: Optional<String?> = Optional.Missing(),
    val roles: Optional<Set<Snowflake>?> = Optional.Missing(),
    val mute: OptionalBoolean? = OptionalBoolean.Missing,
    val deaf: OptionalBoolean? = OptionalBoolean.Missing,
    @SerialName("channel_id")
    val channelId: OptionalSnowflake? = OptionalSnowflake.Missing,
)


@Serializable
data class GuildBanCreateRequest(
    val reason: Optional<String> = Optional.Missing(),
    @SerialName("delete_message_days")
    val deleteMessagesDays: OptionalInt = OptionalInt.Missing,
)

@Serializable
data class GuildRoleCreateRequest(
    val name: Optional<String> = Optional.Missing(),
    val permissions: Optional<Permissions> = Optional.Missing(),
    val color: Optional<Color> = Optional.Missing(),
    @SerialName("hoist")
    val separate: OptionalBoolean = OptionalBoolean.Missing,
    val icon: Optional<String> = Optional.Missing(),
    @SerialName("unicode_emoji")
    val unicodeEmoji: Optional<String> = Optional.Missing(),
    val mentionable: OptionalBoolean = OptionalBoolean.Missing,
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
)


@Serializable(with = GuildRolePositionModifyRequest.Serializer::class)
data class GuildRolePositionModifyRequest(val swaps: List<Pair<Snowflake, Int>>) {

    internal object Serializer : KSerializer<GuildRolePositionModifyRequest> {

        @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor
            get() = listSerialDescriptor(RolePosition.serializer().descriptor)

        override fun serialize(encoder: Encoder, value: GuildRolePositionModifyRequest) {
            val positions = value.swaps.map { RolePosition(it.first, it.second) }
            ListSerializer(RolePosition.serializer()).serialize(encoder, positions)
        }

        override fun deserialize(decoder: Decoder): GuildRolePositionModifyRequest {
            val values = decoder.decodeSerializableValue(ListSerializer(RolePosition.serializer()))
            return GuildRolePositionModifyRequest(values.map { it.id to it.position })
        }

    }

    @Serializable
    private data class RolePosition(val id: Snowflake, val position: Int)
}

@Serializable
data class GuildRoleModifyRequest(
    val name: Optional<String?> = Optional.Missing(),
    val permissions: Optional<Permissions?> = Optional.Missing(),
    val color: Optional<Color?> = Optional.Missing(),
    @SerialName("hoist")
    val separate: OptionalBoolean? = OptionalBoolean.Missing,
    val icon: Optional<String> = Optional.Missing(),
    @SerialName("unicode_emoji")
    val unicodeEmoji: Optional<String> = Optional.Missing(),
    val mentionable: OptionalBoolean? = OptionalBoolean.Missing,
)

@Serializable
data class GuildIntegrationCreateRequest(val type: Int, val id: String)

@Serializable
data class GuildIntegrationModifyRequest(
    @SerialName("expire_behavior")
    val expireBehavior: Optional<IntegrationExpireBehavior> = Optional.Missing(),
    @SerialName("expire_grace_period")
    val expirePeriod: OptionalInt? = OptionalInt.Missing,
    @SerialName("enable_emoticons")
    val emoticons: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable
@DeprecatedSinceKord("0.7.0")
@Deprecated("Guild embeds were renamed to widgets.", ReplaceWith("GuildWidgetModifyRequest"), DeprecationLevel.ERROR)
data class GuildEmbedModifyRequest(
    val enabled: Boolean,
    @SerialName("channel_id")
    val channelId: Snowflake,
)

@Serializable
data class GuildWidgetModifyRequest(
    val enabled: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("channel_id")
    val channelId: OptionalSnowflake? = OptionalSnowflake.Missing,
)

@Serializable
data class CurrentUserNicknameModifyRequest(
    val nick: Optional<String?> = Optional.Missing()
)

@Serializable
data class GuildModifyRequest(
    val name: Optional<String> = Optional.Missing(),
    val region: Optional<String?> = Optional.Missing(),
    @SerialName("verification_level")
    val verificationLevel: Optional<VerificationLevel?> = Optional.Missing(),
    @SerialName("default_message_notifications")
    val defaultMessageNotificationLevel: Optional<DefaultMessageNotificationLevel?> = Optional.Missing(),
    @SerialName("explicit_content_filter")
    val contentFilter: Optional<ExplicitContentFilter?> = Optional.Missing(),
    @SerialName("afk_channel_id")
    val afkChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("afk_timeout")
    val afkTimeout: OptionalInt = OptionalInt.Missing,
    val icon: Optional<String?> = Optional.Missing(),
    @SerialName("owner_id")
    val ownerId: OptionalSnowflake = OptionalSnowflake.Missing,
    val splash: Optional<String?> = Optional.Missing(),
    val banner: Optional<String?> = Optional.Missing(),
    @SerialName("system_channel_id")
    val systemChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("rules_channel_id")
    val rulesChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("public_updates_channel_id")
    val publicUpdatesChannelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("preferred_locale")
    val preferredLocale: Optional<String?> = Optional.Missing(),
)

@Serializable
data class GuildWelcomeScreenModifyRequest(
    val enabled: OptionalBoolean = OptionalBoolean.Missing,
    val welcomeScreenChannels: Optional<List<DiscordWelcomeScreenChannel>> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing()
)

@Serializable
data class GuildScheduledEventCreateRequest(
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    val name: String,
    @SerialName("privacy_level")
    val privacyLevel: StageInstancePrivacyLevel,
    @SerialName("scheduled_start_time")
    val scheduledStartTime: Instant,
    val description: Optional<String> = Optional.Missing(),
    @SerialName("entity_type")
    val entityType: ScheduledEntityType
)
