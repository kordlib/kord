package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

private val MemberData.id get() = "$userId$guildId"

@Serializable
public data class MemberData(
    val userId: Snowflake,
    val guildId: Snowflake,
    val nick: Optional<String?> = Optional.Missing(),
    val roles: List<Snowflake>,
    val joinedAt: Instant,
    val premiumSince: Optional<Instant?> = Optional.Missing(),
    val pending: OptionalBoolean = OptionalBoolean.Missing,
    val avatar: Optional<String?> = Optional.Missing(),
    val communicationDisabledUntil: Optional<Instant?> = Optional.Missing()
) {

    public companion object {
        public val description: DataDescription<MemberData, String> = description(MemberData::id)

        public fun from(userId: Snowflake, guildId: Snowflake, entity: DiscordGuildMember): MemberData = with(entity) {
            MemberData(
                userId = userId,
                guildId = guildId,
                nick = nick,
                roles = roles,
                joinedAt = joinedAt,
                premiumSince = premiumSince,
                pending = pending,
                avatar = avatar,
                communicationDisabledUntil = communicationDisabledUntil,
            )
        }


        public fun from(userId: Snowflake, guildId: Snowflake, entity: DiscordInteractionGuildMember): MemberData =
            with(entity) {
                MemberData(
                    userId = userId,
                    guildId = guildId,
                    nick = nick,
                    roles = roles,
                    joinedAt = joinedAt,
                    premiumSince = premiumSince,
                    pending = pending,
                    avatar = avatar,
                    communicationDisabledUntil = communicationDisabledUntil,
                )
            }

        public fun from(userId: Snowflake, entity: DiscordAddedGuildMember): MemberData = with(entity) {
            MemberData(
                userId = userId,
                guildId = guildId,
                nick = nick,
                roles = roles,
                joinedAt = joinedAt,
                premiumSince = premiumSince,
                pending = pending,
                avatar = avatar,
                communicationDisabledUntil = communicationDisabledUntil,
            )
        }

        public fun from(entity: DiscordUpdatedGuildMember): MemberData = with(entity) {
            MemberData(
                userId = user.id,
                guildId = guildId,
                nick = nick,
                roles = roles,
                joinedAt = joinedAt,
                premiumSince = premiumSince,
                pending = pending,
                avatar = avatar,
                communicationDisabledUntil = communicationDisabledUntil,
            )
        }
    }
}

public fun DiscordGuildMember.toData(userId: Snowflake, guildId: Snowflake): MemberData =
    MemberData.from(userId, guildId, this)

public fun DiscordInteractionGuildMember.toData(userId: Snowflake, guildId: Snowflake): MemberData =
    MemberData.from(userId, guildId, this)
