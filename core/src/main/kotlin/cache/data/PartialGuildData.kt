package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordPartialGuild
import dev.kord.common.entity.GuildFeature
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.map
import kotlinx.serialization.Serializable

@Serializable
class PartialGuildData(
    val id: Snowflake,
    val name: String,
    val icon: String? = null,
    val owner: OptionalBoolean = OptionalBoolean.Missing,
    val permissions: Optional<Permissions> = Optional.Missing(),
    val features: List<GuildFeature>,
    val welcomeScreen: Optional<WelcomeScreenData> = Optional.Missing(),

    ) {
    companion object {

        fun from(partialGuild: DiscordPartialGuild) = with(partialGuild) {
            PartialGuildData(
                id,
                name,
                icon,
                owner,
                permissions,
                features,
                welcomeScreen = welcomeScreen.map { WelcomeScreenData.from(it) },
            )
        }
    }


}
