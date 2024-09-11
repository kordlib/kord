package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.WelcomeScreenData
import dev.kord.core.entity.channel.WelcomeScreenChannel

/**
 * Shown to new members in community guild, returned when in the invite object.
 *
 * @param data The [WelcomeScreenData] for the Welcome screen
 */
public class WelcomeScreen(public val data: WelcomeScreenData, override val kord: Kord) : KordObject {

    /**
     * The server description shown in the welcome screen.
     */
    public val description: String? get() = data.description

    /**
     * The channels shown in the welcome screen.
     */
    public val welcomeScreenChannels: List<WelcomeScreenChannel>
        get() = data.welcomeChannels.map { WelcomeScreenChannel(it, kord) }

}
