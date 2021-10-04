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
public class PartialGuildData(
    public val id: Snowflake,
    public val name: String,
    public val icon: String? = null,
    public val owner: OptionalBoolean = OptionalBoolean.Missing,
    public val permissions: Optional<Permissions> = Optional.Missing(),
    public val features: List<GuildFeature>,
    public val welcomeScreen: Optional<WelcomeScreenData> = Optional.Missing(),
) {
    public companion object {
        public fun from(partialGuild: DiscordPartialGuild): PartialGuildData = with(partialGuild) {
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
