package dev.kord.rest.builder.guild

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildModifyRequest
import kotlin.time.Duration

@KordDsl
public class GuildModifyBuilder : AuditRequestBuilder<GuildModifyRequest> {
    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    public var name: String? by ::_name.delegate()

    private var _region: Optional<String?> = Optional.Missing()
    public var region: String? by ::_region.delegate()

    private var _verificationLevel: Optional<VerificationLevel?> = Optional.Missing()
    public var verificationLevel: VerificationLevel? by ::_verificationLevel.delegate()

    private var _notificationLevel: Optional<DefaultMessageNotificationLevel?> = Optional.Missing()
    public var notificationLevel: DefaultMessageNotificationLevel? by ::_notificationLevel.delegate()

    private var _explicitContentFilter: Optional<ExplicitContentFilter?> = Optional.Missing()
    public var explicitContentFilter: ExplicitContentFilter? by ::_explicitContentFilter.delegate()

    private var _afkChannelId: OptionalSnowflake? = OptionalSnowflake.Missing
    public var afkChannelId: Snowflake? by ::_afkChannelId.delegate()

    private var _afkTimeout: Optional<Duration> = Optional.Missing()
    public var afkTimeout: Duration? by ::_afkTimeout.delegate()

    private var _icon: Optional<Image?> = Optional.Missing()
    public var icon: Image? by ::_icon.delegate()

    private var _ownerId: OptionalSnowflake = OptionalSnowflake.Missing
    public var ownerId: Snowflake? by ::_ownerId.delegate()

    private var _splash: Optional<Image?> = Optional.Missing()
    public var splash: Image? by ::_splash.delegate()

    private var _banner: Optional<Image?> = Optional.Missing()
    public var banner: Image? by ::_banner.delegate()

    private var _systemChannelId: OptionalSnowflake? = OptionalSnowflake.Missing
    public var systemChannelId: Snowflake? by ::_systemChannelId.delegate()

    private var _rulesChannelId: OptionalSnowflake? = OptionalSnowflake.Missing

    /**
     * The id of the channel where "PUBLIC" guilds display rules and/or guidelines.
     */
    public var rulesChannelId: Snowflake? by ::_rulesChannelId.delegate()


    private var _publicUpdatesChannelId: OptionalSnowflake? = OptionalSnowflake.Missing

    /**
     * The id of the channel where admins and moderators of "PUBLIC" guilds receive notices from Discord.
     */
    public var publicUpdatesChannelId: Snowflake? by ::_publicUpdatesChannelId.delegate()

    private var _preferredLocale: Optional<Locale?> = Optional.Missing()

    /**
     * The preferred locale of a "PUBLIC" guild used in server discovery and notices from Discord; defaults to "en-US".
     */
    public var preferredLocale: Locale? by ::_preferredLocale.delegate()

    private var _features: Optional<Set<GuildFeature>> = Optional.Missing()

    /** The enabled [GuildFeature]s. */
    public var features: Set<GuildFeature>? by ::_features.delegate()

    private var _safetyAlertsChannelId: OptionalSnowflake? = OptionalSnowflake.Missing

    /** The id of the channel where admins and moderators of Community guilds receive safety alerts from Discord. */
    public var safetyAlertsChannelId: Snowflake? by ::_safetyAlertsChannelId.delegate()

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
        _preferredLocale.map { locale -> "${locale.language}${locale.country?.let { "-$it" } ?: ""}" },
        features = _features,
        safetyAlertsChannelId = _safetyAlertsChannelId,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GuildModifyBuilder

        if (reason != other.reason) return false
        if (name != other.name) return false
        if (region != other.region) return false
        if (verificationLevel != other.verificationLevel) return false
        if (notificationLevel != other.notificationLevel) return false
        if (explicitContentFilter != other.explicitContentFilter) return false
        if (afkChannelId != other.afkChannelId) return false
        if (afkTimeout != other.afkTimeout) return false
        if (icon != other.icon) return false
        if (ownerId != other.ownerId) return false
        if (splash != other.splash) return false
        if (banner != other.banner) return false
        if (systemChannelId != other.systemChannelId) return false
        if (rulesChannelId != other.rulesChannelId) return false
        if (publicUpdatesChannelId != other.publicUpdatesChannelId) return false
        if (preferredLocale != other.preferredLocale) return false
        if (features != other.features) return false
        if (safetyAlertsChannelId != other.safetyAlertsChannelId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reason?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (region?.hashCode() ?: 0)
        result = 31 * result + (verificationLevel?.hashCode() ?: 0)
        result = 31 * result + (notificationLevel?.hashCode() ?: 0)
        result = 31 * result + (explicitContentFilter?.hashCode() ?: 0)
        result = 31 * result + (afkChannelId?.hashCode() ?: 0)
        result = 31 * result + (afkTimeout?.hashCode() ?: 0)
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + (ownerId?.hashCode() ?: 0)
        result = 31 * result + (splash?.hashCode() ?: 0)
        result = 31 * result + (banner?.hashCode() ?: 0)
        result = 31 * result + (systemChannelId?.hashCode() ?: 0)
        result = 31 * result + (rulesChannelId?.hashCode() ?: 0)
        result = 31 * result + (publicUpdatesChannelId?.hashCode() ?: 0)
        result = 31 * result + (preferredLocale?.hashCode() ?: 0)
        result = 31 * result + (features?.hashCode() ?: 0)
        result = 31 * result + (safetyAlertsChannelId?.hashCode() ?: 0)
        return result
    }

}
