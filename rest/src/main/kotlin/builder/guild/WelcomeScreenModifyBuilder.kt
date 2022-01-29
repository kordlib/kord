package dev.kord.rest.builder.guild

import dev.kord.common.entity.DiscordWelcomeScreenChannel
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildWelcomeScreenModifyRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class WelcomeScreenModifyBuilder : AuditRequestBuilder<GuildWelcomeScreenModifyRequest> {

    override var reason: String? = null

    private var _enabled: OptionalBoolean = OptionalBoolean.Missing

    public var enabled: Boolean? by ::_enabled.delegate()
    private var _description: Optional<String> = Optional.Missing()

    public var description: String? by ::_description.delegate()
    private var _welcomeScreenChannels: Optional<MutableList<WelcomeScreenChannelBuilder>> = Optional.Missing()

    public var welcomeScreenChannels: MutableList<WelcomeScreenChannelBuilder>? by ::_welcomeScreenChannels.delegate()

    public inline fun welcomeChannel(
        id: Snowflake,
        description: String,
        builder: WelcomeScreenChannelBuilder.() -> Unit,
    ) {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        if (welcomeScreenChannels == null) welcomeScreenChannels = mutableListOf()
        welcomeScreenChannels!!.add(WelcomeScreenChannelBuilder(id, description, null, null).apply(builder))
    }

    override fun toRequest(): GuildWelcomeScreenModifyRequest {
        return GuildWelcomeScreenModifyRequest(
            _enabled,
            _welcomeScreenChannels.mapList { it.toRequest() },
            _description
        )
    }

}


public class WelcomeScreenChannelBuilder(
    public var channelId: Snowflake,
    public var description: String,
    public var emojiId: Snowflake?,
    public var emojiName: String?,
) : RequestBuilder<DiscordWelcomeScreenChannel> {
    override fun toRequest(): DiscordWelcomeScreenChannel {
        return DiscordWelcomeScreenChannel(channelId, description, emojiId, emojiName)
    }

}
