package dev.kord.rest.builder.integration

import dev.kord.common.entity.IntegrationExpireBehavior
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildIntegrationModifyRequest

/**
 * Builder for [modifying an integration](https://discord.com/developers/docs/resources/guild#modify-guild-integration).
 */
class IntegrationModifyBuilder : RequestBuilder<GuildIntegrationModifyRequest> {

    private var _expireBehavior: Optional<IntegrationExpireBehavior> = Optional.Missing()

    /**
     * the behavior when an integration subscription lapses.
     */
    var expireBehavior: IntegrationExpireBehavior? by ::_expireBehavior.delegate()

    private var _expirePeriodInDays: OptionalInt = OptionalInt.Missing

    /**
     * 	Period in days where the integration will ignore lapsed subscriptions
     */
    var expirePeriodInDays: Int? by ::_expirePeriodInDays.delegate()

    private var _enableEmoticons: OptionalBoolean = OptionalBoolean.Missing

    /**
     * whether emoticons should be synced for this integration (twitch only currently).
     */
    var enableEmoticons: Boolean? by ::_enableEmoticons.delegate()

    override fun toRequest(): GuildIntegrationModifyRequest = GuildIntegrationModifyRequest(
            _expireBehavior, _expirePeriodInDays, _enableEmoticons
    )

}