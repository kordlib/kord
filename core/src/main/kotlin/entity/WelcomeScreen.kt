package dev.kord.core.entity

import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.WelcomeScreenData
import dev.kord.core.entity.channel.WelcomeScreenChannel

class WelcomeScreen(val data: WelcomeScreenData, override val kord: Kord) : KordObject {

    val description: String? get() = data.description

    val welcomeScreenChanenls: List<WelcomeScreenChannel>
        get() = data.welcomeChannels.map { WelcomeScreenChannel(it, kord) }

}
