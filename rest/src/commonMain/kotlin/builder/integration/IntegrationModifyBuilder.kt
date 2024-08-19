package dev.kord.rest.builder.integration

import dev.kord.common.annotation.KordDsl
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
@KordDsl
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as IntegrationModifyBuilder

        if (reason != other.reason) return false
        if (expireBehavior != other.expireBehavior) return false
        if (expirePeriodInDays != other.expirePeriodInDays) return false
        if (enableEmoticons != other.enableEmoticons) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reason?.hashCode() ?: 0
        result = 31 * result + (expireBehavior?.hashCode() ?: 0)
        result = 31 * result + (expirePeriodInDays ?: 0)
        result = 31 * result + (enableEmoticons?.hashCode() ?: 0)
        return result
    }

}
