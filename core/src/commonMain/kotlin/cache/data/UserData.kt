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

private val ThreadMemberData.nullableUserId get() = userId.value
private val EntitlementData.nullableUserId get() = userId.value

@Serializable
public data class UserData(
    val id: Snowflake,
    val username: String,
    val discriminator: Optional<String> = Optional.Missing(),
    val globalName: Optional<String?> = Optional.Missing(),
    val avatar: String? = null,
    val bot: OptionalBoolean = OptionalBoolean.Missing,
    val publicFlags: Optional<UserFlags> = Optional.Missing(),
    val banner: String? = null,
    val accentColor: Int? = null,
    val avatarDecoration: Optional<String?> = Optional.Missing()
) {
    public companion object {

        public val description: DataDescription<UserData, Snowflake> = description(UserData::id) {
            link(UserData::id to MemberData::userId)
            link(UserData::id to ThreadMemberData::nullableUserId)
            link(UserData::id to VoiceStateData::userId)
            link(UserData::id to PresenceData::userId)
            link(UserData::id to EntitlementData::nullableUserId)
        }

        public fun from(entity: DiscordUser): UserData = with(entity) {
            UserData(id, username, discriminator, globalName, avatar, bot, publicFlags, banner, accentColor, avatarDecoration)
        }

        public fun from(entity: DiscordOptionallyMemberUser): UserData = with(entity) {
            UserData(id, username, discriminator, globalName, avatar, bot, publicFlags)
        }

    }
}

public fun DiscordUser.toData(): UserData = UserData.from(this)
