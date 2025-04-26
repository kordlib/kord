package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A representation of a [Discord Emoji structure](https://discord.com/developers/docs/resources/emoji#emoji-object).
 *
 * @param id The emoji id.
 * @param name The emoji name.
 * @param roles The roles tis emoji is whitelisted to.
 * @param user The user that created this emoji.
 * @param requireColons Whether this emoji must be wrapped in colons.
 * @param managed Whether this emoji is managed.
 * @param animated Whether this emoji is animated.
 * @param available Whether this emoji can be used, may be false due to loss of [boost level][PremiumTier].
 */
@Serializable
public data class DiscordEmoji(
    val id: Snowflake?,
    val name: String?,
    val roles: Optional<List<Snowflake>> = Optional.Missing(),
    val user: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("require_colons")
    val requireColons: OptionalBoolean = OptionalBoolean.Missing,
    val managed: OptionalBoolean = OptionalBoolean.Missing,
    val animated: OptionalBoolean = OptionalBoolean.Missing,
    val available: OptionalBoolean = OptionalBoolean.Missing,
)

@Serializable
public data class DiscordUpdatedEmojis(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val emojis: List<DiscordEmoji>,
)


/**
 * A partial representation of a [Discord Emoji structure](https://discord.com/developers/docs/resources/emoji#emoji-object).
 *
 * @param id The emoji id.
 * @param name The emoji name.
 * @param animated Whether this emoji is animated.
 */
@Serializable
public data class DiscordPartialEmoji(
    val id: Snowflake? = null,
    val name: String? = null,
    val animated: OptionalBoolean = OptionalBoolean.Missing,
)
