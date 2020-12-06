package dev.kord.rest.builder.guild

import dev.kord.common.entity.DefaultMessageNotificationLevel
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.VerificationLevel
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildModifyRequest
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ExplicitContentFilter
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import java.util.*

@KordDsl
class GuildModifyBuilder : AuditRequestBuilder<GuildModifyRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    var name: String? by ::_name.delegate()

    private var _region: Optional<String?> = Optional.Missing()
    var region: String? by ::_region.delegate()

    private var _verificationLevel: Optional<VerificationLevel?> = Optional.Missing()
    var verificationLevel: VerificationLevel? by ::_verificationLevel.delegate()

    private var _notificationLevel: Optional<DefaultMessageNotificationLevel?> = Optional.Missing()
    var notificationLevel: DefaultMessageNotificationLevel? by ::_notificationLevel.delegate()

    private var _explicitContentFilter: Optional<ExplicitContentFilter?> = Optional.Missing()
    var explicitContentFilter: ExplicitContentFilter? by ::_explicitContentFilter.delegate()

    private var _afkChannelId: OptionalSnowflake? = OptionalSnowflake.Missing
    var afkChannelId: Snowflake? by ::_afkChannelId.delegate()

    private var _afkTimeout: OptionalInt = OptionalInt.Missing
    var afkTimeout: Int? by ::_afkTimeout.delegate()

    private var _icon: Optional<Image?> = Optional.Missing()
    var icon: Image? by ::_icon.delegate()

    private var _ownerId: OptionalSnowflake = OptionalSnowflake.Missing
    var ownerId: Snowflake? by ::_ownerId.delegate()

    private var _splash: Optional<Image?> = Optional.Missing()
    var splash: Image? by ::_splash.delegate()

    private var _banner: Optional<Image?> = Optional.Missing()
    var banner: Image? by ::_banner.delegate()

    private var _systemChannelId: OptionalSnowflake? = OptionalSnowflake.Missing
    var systemChannelId: Snowflake? by ::_systemChannelId.delegate()

    private var _rulesChannelId: OptionalSnowflake? = OptionalSnowflake.Missing
    /**
     * The id of the channel where "PUBLIC" guilds display rules and/or guidelines.
     */
    var rulesChannelId: Snowflake? by ::_rulesChannelId.delegate()


    private var _publicUpdatesChannelId: OptionalSnowflake? = OptionalSnowflake.Missing
    /**
     * The id of the channel where admins and moderators of "PUBLIC" guilds receive notices from Discord.
     */
    var publicUpdatesChannelId: Snowflake? by ::_publicUpdatesChannelId.delegate()

    private var _preferredLocale: Optional<Locale?> = Optional.Missing()
    /**
     * The preferred locale of a "PUBLIC" guild used in server discovery and notices from Discord; defaults to "en-US".
     */
    var preferredLocale: Locale? by ::_preferredLocale.delegate()

    override fun toRequest(): GuildModifyRequest = GuildModifyRequest(
            _name,
            _region,
            _verificationLevel,
            _notificationLevel,
            _explicitContentFilter,
            _afkChannelId,
            _afkTimeout,
            _icon.map { it.dataUri },
            _ownerId,
            _splash.map { it.dataUri },
            _banner.map { it.dataUri },
            _systemChannelId,
            _rulesChannelId,
            _publicUpdatesChannelId,
            _preferredLocale.map { "${it.language}-${it.country}" }
    )
}