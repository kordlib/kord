package dev.kord.rest.json.request

import dev.kord.common.Color
import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
public data class GuildCreateRequest(
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
    val afkTimeout: Optional<DurationInSeconds> = Optional.Missing(),
    @SerialName("system_channel_id")
    val systemChannelId: OptionalSnowflake = OptionalSnowflake.Missing
)

@Serializable
public data class GuildChannelCreateRequest(
    val name: String,
    val type: ChannelType,
    val topic: Optional<String> = Optional.Missing(),
    val bitrate: OptionalInt = OptionalInt.Missing,
    @SerialName("user_limit")
    val userLimit: OptionalInt = OptionalInt.Missing,
    @SerialName("rate_limit_per_user")
    val rateLimitPerUser: Optional<DurationInSeconds> = Optional.Missing(),
    val position: OptionalInt = OptionalInt.Missing,
    @SerialName("permission_overwrites")
    val permissionOverwrite: Optional<Set<Overwrite>> = Optional.Missing(),
    @SerialName("parent_id")
    val parentId: OptionalSnowflake = OptionalSnowflake.Missing,
    val nsfw: OptionalBoolean = OptionalBoolean.Missing,
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("default_auto_archive_duration")
    val defaultAutoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing(),
)

@Serializable
public data class ChannelPositionSwapRequest(
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
public data class GuildChannelPositionModifyRequest(
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
public data class GuildMemberAddRequest(
    @SerialName("access_token")
    val token: String,
    val nick: Optional<String> = Optional.Missing(),
    val roles: Optional<Set<Snowflake>> = Optional.Missing(),
    val mute: OptionalBoolean = OptionalBoolean.Missing,
    val deaf: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant> = Optional.Missing()
)

@Serializable
public data class GuildMemberModifyRequest(
    val nick: Optional<String?> = Optional.Missing(),
    val roles: Optional<Set<Snowflake>?> = Optional.Missing(),
    val mute: OptionalBoolean? = OptionalBoolean.Missing,
    val deaf: OptionalBoolean? = OptionalBoolean.Missing,
    @SerialName("channel_id")
    val channelId: OptionalSnowflake? = OptionalSnowflake.Missing,
    @SerialName("communication_disabled_until")
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
)


@Serializable
public data class GuildBanCreateRequest(
    val reason: Optional<String> = Optional.Missing(),
    @SerialName("delete_message_days")
    val deleteMessagesDays: OptionalInt = OptionalInt.Missing,
)

@Serializable
public data class GuildRoleCreateRequest(
    val name: Optional<String> = Optional.Missing(),
    val permissions: Optional<Permissions> = Optional.Missing(),
    val color: Optional<Color> = Optional.Missing(),
    val hoist: OptionalBoolean = OptionalBoolean.Missing,
    val icon: Optional<String?> = Optional.Missing(),
    @SerialName("unicode_emoji")
    val unicodeEmoji: Optional<String?> = Optional.Missing(),
    val mentionable: OptionalBoolean = OptionalBoolean.Missing,
    /** Only use this when creating a guild with roles. */
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
) {
    @Deprecated("Renamed to 'hoist'.", ReplaceWith("this.hoist"), DeprecationLevel.ERROR)
    public val separate: OptionalBoolean
        get() = hoist
}


@Serializable(with = GuildRolePositionModifyRequest.Serializer::class)
public data class GuildRolePositionModifyRequest(val swaps: List<Pair<Snowflake, Int>>) {

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
public data class GuildRoleModifyRequest(
    val name: Optional<String?> = Optional.Missing(),
    val permissions: Optional<Permissions?> = Optional.Missing(),
    val color: Optional<Color?> = Optional.Missing(),
    val hoist: OptionalBoolean? = OptionalBoolean.Missing,
    val icon: Optional<String?> = Optional.Missing(),
    @SerialName("unicode_emoji")
    val unicodeEmoji: Optional<String?> = Optional.Missing(),
    val mentionable: OptionalBoolean? = OptionalBoolean.Missing,
) {
    @Deprecated("Renamed to 'hoist'.", ReplaceWith("this.hoist"), DeprecationLevel.ERROR)
    public val separate: OptionalBoolean?
        get() = hoist
}

@Serializable
public data class GuildIntegrationCreateRequest(val type: Int, val id: String)

@Serializable
public data class GuildIntegrationModifyRequest(
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
public data class GuildEmbedModifyRequest(
    val enabled: Boolean,
    @SerialName("channel_id")
    val channelId: Snowflake,
)

@Serializable
public data class GuildWidgetModifyRequest(
    val enabled: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("channel_id")
    val channelId: OptionalSnowflake? = OptionalSnowflake.Missing,
)

@Serializable
public data class CurrentUserNicknameModifyRequest(
    val nick: Optional<String?> = Optional.Missing()
)

@Serializable
public data class GuildModifyRequest(
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
    val afkTimeout: Optional<DurationInSeconds> = Optional.Missing(),
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
public data class GuildWelcomeScreenModifyRequest(
    val enabled: OptionalBoolean = OptionalBoolean.Missing,
    val welcomeScreenChannels: Optional<List<DiscordWelcomeScreenChannel>> = Optional.Missing(),
    val description: Optional<String> = Optional.Missing()
)


@Serializable
public data class GuildScheduledEventUsersResponse(
    @SerialName("guild_scheduled_event_id")
    val guildScheduledEventId: Snowflake,
    val user: DiscordUser,
    val member: Optional<DiscordGuildMember> = Optional.Missing(),
)
