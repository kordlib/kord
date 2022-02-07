package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordOptionallyMemberUser
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.UserFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.Serializable

private val WebhookData.nullableUserId get() = userId.value

@Serializable
public data class UserData(
    val id: Snowflake,
    val username: String,
    val discriminator: String,
    val avatar: String? = null,
    val bot: OptionalBoolean = OptionalBoolean.Missing,
    val publicFlags: Optional<UserFlags> = Optional.Missing(),
    val banner: String? = null,
    val accentColor: Int? = null
) {
    public companion object {

        public val description: DataDescription<UserData, Snowflake>
            get() = description(UserData::id) {
                link(UserData::id to MemberData::userId)
                link(UserData::id to WebhookData::nullableUserId)
                link(UserData::id to VoiceStateData::userId)
                link(UserData::id to PresenceData::userId)
            }

        public fun from(entity: DiscordUser): UserData = with(entity) {
            UserData(id, username, discriminator, avatar, bot, publicFlags, banner, accentColor)
        }

        @Suppress("DeprecatedCallableAddReplaceWith", "DEPRECATION")
        @Deprecated("'DiscordInviteUser' is deprecated. Use 'DiscordUser' instead.")
        public fun from(entity: dev.kord.gateway.DiscordInviteUser): UserData = with(entity) {
            UserData(id, username, discriminator, avatar, bot, publicFlags)
        }

        public fun from(entity: DiscordOptionallyMemberUser): UserData = with(entity) {
            UserData(id, username, discriminator, avatar, bot, publicFlags)
        }

    }
}

public fun DiscordUser.toData(): UserData = UserData.from(this)
