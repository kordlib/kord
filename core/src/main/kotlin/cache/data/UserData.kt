package dev.kord.core.cache.data

import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordOptionallyMemberUser
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.UserFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.gateway.DiscordInviteUser
import kotlinx.serialization.Serializable

private val WebhookData.nullableUserId get() = userId.value

@Serializable
data class UserData(
    val id: Snowflake,
    val username: String,
    val discriminator: String,
    val avatar: String? = null,
    val bot: OptionalBoolean = OptionalBoolean.Missing,
    val publicFlags: Optional<UserFlags> = Optional.Missing(),
) {
    companion object {

        val description
            get() = description(UserData::id) {
                link(UserData::id to MemberData::userId)
                link(UserData::id to WebhookData::nullableUserId)
                link(UserData::id to VoiceStateData::userId)
                link(UserData::id to PresenceData::userId)
            }

        fun from(entity: DiscordUser) = with(entity) {
            UserData(id, username, discriminator, avatar, bot, publicFlags)
        }

        fun from(entity: DiscordInviteUser) = with(entity) {
            UserData(id, username, discriminator, avatar, bot, publicFlags)
        }

        fun from(entity: DiscordOptionallyMemberUser) = with(entity) {
            UserData(id, username, discriminator, avatar, bot, publicFlags)
        }

    }
}

fun DiscordUser.toData() = UserData.from(this)