package dev.kord.rest.builder.integration

import dev.kord.common.entity.IntegrationExpireBehavior
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildIntegrationModifyRequest

/**
 * Builder for [modifying an integration](https://discord.com/developers/docs/resources/guild#modify-guild-integration).
 */
public class IntegrationModifyBuilder : AuditRequestBuilder<GuildIntegrationModifyRequest> {

    override var reason: String? = null

    private var _expireBehavior: Optional<IntegrationExpireBehavior> = Optional.Missing()

    /**
     * the behavior when an integration subscription lapses.
     */
    public var expireBehavior: IntegrationExpireBehavior? by ::_expireBehavior.delegate()

    private var _expirePeriodInDays: OptionalInt = OptionalInt.Missing

    /**
     * 	Period in days where the integration will ignore lapsed subscriptions
     */
    public var expirePeriodInDays: Int? by ::_expirePeriodInDays.delegate()

    private var _enableEmoticons: OptionalBoolean = OptionalBoolean.Missing

    /**
     * whether emoticons should be synced for this integration (twitch only currently).
     */
    public var enableEmoticons: Boolean? by ::_enableEmoticons.delegate()

    override fun toRequest(): GuildIntegrationModifyRequest = GuildIntegrationModifyRequest(
        _expireBehavior, _expirePeriodInDays, _enableEmoticons
    )

}
