package dev.kord.rest.builder.guild

import dev.kord.common.entity.DiscordWelcomeScreenChannel
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildWelcomeScreenModifyRequest

class WelcomeScreenModifyBuilder : RequestBuilder<GuildWelcomeScreenModifyRequest> {

    private var _enabled: OptionalBoolean = OptionalBoolean.Missing
    var enabled: Boolean? by ::_enabled.delegate()

    private var _description: Optional<String> = Optional.Missing()
    var description: String? by ::_description.delegate()

    private var _welcomeScreenChannels: Optional<MutableList<DiscordWelcomeScreenChannel>> = Optional.Missing()
    private var welcomeScreenChannels: MutableList<DiscordWelcomeScreenChannel>? by ::_welcomeScreenChannels.delegate()

    fun welcomeChannel(id: Snowflake, description: String, builder: DiscordWelcomeScreenChannel.() -> Unit) {

        if (welcomeScreenChannels == null) welcomeScreenChannels = mutableListOf()
        welcomeScreenChannels!!.add(DiscordWelcomeScreenChannel(id, description, null, null).apply(builder))
    }

    override fun toRequest(): GuildWelcomeScreenModifyRequest {
        return GuildWelcomeScreenModifyRequest(_enabled, _welcomeScreenChannels, _description)
    }

}