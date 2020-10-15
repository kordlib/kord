package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildMessageChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.TextChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.VoiceChannelBehavior
import com.gitlab.kordlib.core.cache.data.GuildData
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import com.gitlab.kordlib.core.toSnowflakeOrNull
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.service.RestClient
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * An instance of a [Discord Guild](https://discord.com/developers/docs/resources/guild).
 */
class Guild(
        val data: GuildData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : GuildBehavior {

    override val id: Snowflake get() = Snowflake(data.id)

    /**
     * The id of the afk voice channel, if present.
     */
    val afkChannelId: Snowflake? get() = data.afkChannelId.toSnowflakeOrNull()

    val afkChannel: VoiceChannelBehavior?
        get() = afkChannelId?.let { VoiceChannelBehavior(guildId = id, id = it, kord = kord) }

    /**
     * The afk timeout in seconds.
     */
    val afkTimeout: Int get() = data.afkTimeout

    /**
     *  The id of the guild creator if it is bot-created.
     */
    val applicationId: Snowflake? get() = data.applicationId.toSnowflakeOrNull()

    /**
     * The approximate number of members in this guild. Present if this guild was requested through
     * [rest][com.gitlab.kordlib.rest.service.RestClient] with the flag `with_counts`.
     */
    val approximateMemberCount: Int? get() = data.approximateMemberCount

    /**
     * The approximate number of online members in this guild. Present if this guild was requested through
     * [rest][com.gitlab.kordlib.rest.service.RestClient] with the flag `with_counts`.
     */
    val approximatePresenceCount: Int? get() = data.approximatePresenceCount

    /**
     * The banner hash, if present.
     */
    val bannerHash: String? get() = data.banner

    /**
     * The ids of all [channels][GuildChannel].
     */
    val channelIds: Set<Snowflake> get() = data.channels.asSequence().map { Snowflake(it) }.toSet()

    /**
     * The explicit content filter level.
     */
    val contentFilter: ExplicitContentFilter get() = data.explicitContentFilter

    /**
     * The description of this guild, if present.
     */
    val description: String? get() = data.description

    /**
     * The ID of the embedded channel, if present.
     */
    val embedChannelId: Snowflake? get() = data.embedChannelId?.let(::Snowflake)

    /**
     * The behavior of the embedded channel, if present.
     */
    val embedChannel: GuildChannelBehavior?
        get() = embedChannelId?.let { GuildChannelBehavior(guildId = id, id = it, kord = kord) }

    /**
     * The ids of custom emojis in this guild.
     */
    val emojiIds: Set<Snowflake> get() = data.emojis.asSequence().map { it.id.toSnowflakeOrNull() }.filterNotNull().toSet()

    /**
     * The custom emojis in this guild.
     */
    val emojis: List<GuildEmoji> get() = data.emojis.map { GuildEmoji(it, kord) }

    /**
     * The behavior of the @everyone role.
     */
    val everyoneRole: RoleBehavior get() = RoleBehavior(id, id, kord)

    /**
     * The enabled guild features.
     */
    val features: Set<GuildFeature> get() = data.features.toSet()

    /**
     * The icon hash, if present.
     */
    val iconHash: String? get() = data.icon

    /**
     * The time at which this guild was joined, if present.
     */
    val joinedTime: Instant? get() = data.joinedAt?.let { DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(it, Instant::from) }

    /**
     * The id of the owner.
     */
    val ownerId: Snowflake get() = Snowflake(data.ownerId)

    /**
     * The behavior of the owner.
     */
    val owner: MemberBehavior get() = MemberBehavior(guildId = id, id = ownerId, kord = kord)

    /**
     * The number of members in the guild, if present.
     *
     *  > This value is only present when the [Kord.cache] stores guilds and the client has been logged in. It
     *  will not be updated throughout the lifetime of the gateway and should thus be seen as an approximation rather
     *  than a precise value.
     */
    val memberCount: Int? get() = data.memberCount

    /**
     * The required multi-factor authentication level of this guild.
     */
    val mfaLevel: MFALevel get() = data.mfaLevel

    /**
     * The name of this guild.
     */
    val name: String get() = data.name

    /**
     * the id of the channel where guild notices such as welcome messages and boost events are posted.
     */
    val publicUpdatesChannelId: Snowflake? get() = data.publicUpdatesChannelId.toSnowflakeOrNull()

    /**
     * The behavior of the channel where guild notices such as welcome messages and boost events are posted.
     */
    val publicUpdatesChannel: GuildMessageChannelBehavior?
        get() = publicUpdatesChannelId?.let {
            GuildMessageChannelBehavior(guildId = id, id = it, kord = kord)
        }

    val preferredLocale: Locale get() = Locale.forLanguageTag(data.preferredLocale)

    /**
     * The behaviors of all [channels][GuildChannel].
     */
    val channelBehaviors: Set<GuildChannelBehavior>
        get() = data.channels.asSequence().map { GuildChannelBehavior(id = Snowflake(it), guildId = id, kord = kord) }.toSet()

    /**
     * The default message notification level.
     */
    val defaultMessageNotificationLevel: DefaultMessageNotificationLevel get() = data.defaultMessageNotifications

    /**
     * The voice region id for the guild.
     */
    val regionId: Snowflake get() = Snowflake(data.region)

    /**
     * The id of the channel in which a discoverable server's rules should be found
     **/
    val rulesChannelId: Snowflake? get() = data.rulesChannelId.toSnowflakeOrNull()

    /**
     * The channel behavior in which a discoverable server's rules should be found.
     **/
    val rulesChannel: GuildMessageChannelBehavior?
        get() = data.rulesChannelId.toSnowflakeOrNull()?.let {
            GuildMessageChannelBehavior(id, it, kord)
        }

    /**
     * The splash hash, if present.
     */
    val splashHash: String? get() = data.splash

    /**
     * The hash of the discovery splash, if present.
     */
    val discoverySplashHash: String? get() = data.discoverySplash

    /**
     * The id of the channel to which system messages are sent.
     */
    val systemChannelId: Snowflake? get() = data.systemChannelId.toSnowflakeOrNull()

    /**
     * The behavior of the channel to which system messages are sent.
     */
    val systemChannel: TextChannelBehavior?
        get() = systemChannelId?.let {
            TextChannelBehavior(guildId = id, id = it, kord = kord)
        }

    val systemChannelFlags: SystemChannelFlags get() = data.systemChannelFlags ?: SystemChannelFlags(0)

    /**
     * The verification level required for the guild.
     */
    val verificationLevel: VerificationLevel get() = data.verificationLevel

    /**
     * The ids of the roles.
     */
    val roleIds: Set<Snowflake> get() = data.roles.asSequence().map { Snowflake(it) }.toSet()

    /**
     * The behaviors of the [roles][Role].
     */
    val roleBehaviors: Set<RoleBehavior> get() = data.roles.asSequence().map { RoleBehavior(id = Snowflake(it), guildId = id, kord = kord) }.toSet()

    /**
     * The vanity code of this server used in the [vanityUrl], if present.
     */
    val vanityCode: String? get() = data.vanityUrlCode

    /**
     * The vanity invite URL of this server, if present.
     */
    val vanityUrl: String? get() = data.vanityUrlCode?.let { "https://discord.gg/$it" }

    /**
     * Requests to get the [VoiceChannel] represented by the [afkChannelId],
     * returns null if the [afkChannelId] isn't present or the channel itself isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [VoiceChannel] wasn't present.
     */
    suspend fun getAfkChannel(): VoiceChannel? = afkChannelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Gets the banner url in the specified format.
     */
    fun getBannerUrl(format: Image.Format): String? = data.banner?.let { "https://cdn.discordapp.com/banners/${id.value}/$it.${format.extension}" }

    /**
     * Requests to get the banner image in the specified [format], if present.
     */
    suspend fun getBanner(format: Image.Format): Image? {
        val url = getBannerUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }

    /**
     * Requests to get the [GuildChannel] represented by the [embedChannel],
     * returns null if the [GuildChannel] isn't present or [embedChannel] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getEmbedChannel(): GuildChannel? = embedChannelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Requests to get the [GuildEmoji] represented by the [emojiId] in this guild.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [GuildEmoji] wasn't present.
     */
    suspend fun getEmoji(emojiId: Snowflake): GuildEmoji =
            supplier.getEmoji(guildId = id, emojiId = emojiId)

    /**
     * Requests to get the [GuildEmoji] represented by the [emojiId] in this guild,
     * returns null if the [GuildEmoji] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getEmojiOrNull(emojiId: Snowflake): GuildEmoji? =
            supplier.getEmojiOrNull(guildId = id, emojiId = emojiId)


    /**
     * Requests to get the `@everyone` [Role].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Role] wasn't present.
     */
    suspend fun getEveryoneRole(): Role = supplier.getRole(id, id)

    /**
     * Requests to get the `@everyone` [Role],
     * returns null if the [Role] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getEveryoneRoleOrNull(): Role? = supplier.getRoleOrNull(id, id)

    /**
     * Gets the discovery splash url in the specified [format], if present.
     */
    fun getDiscoverySplashUrl(format: Image.Format): String? =
            data.splash?.let { "discovery-splashes/${id.value}/${it}.${format.extension}" }

    /**
     * Requests to get the splash image in the specified [format], if present.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     */
    suspend fun getDiscoverySplash(format: Image.Format): Image? {
        val url = getDiscoverySplashUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }

    /**
     * Gets the icon url, if present.
     */
    fun getIconUrl(format: Image.Format): String? = data.icon?.let { "https://cdn.discordapp.com/icons/${id.value}/$it.${format.extension}" }

    /**
     * Requests to get the icon image in the specified [format], if present.
     */
    suspend fun getIcon(format: Image.Format): Image? {
        val url = getIconUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }

    /**
     * Requests to get the owner of this guild as a [Member].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Member] wasn't present.
     */
    suspend fun getOwner(): Member = supplier.getMember(id, ownerId)

    /**
     * Requests to get The channel where guild notices such as welcome messages and boost events are posted.
     */
    suspend fun getPublicUpdatesChannel(): GuildMessageChannel? = publicUpdatesChannel?.asChannel()

    /**
     * Requests to get the owner of this guild as a [Member],
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getOwnerOrNull(): Member? = supplier.getMemberOrNull(id, ownerId)

    /**
     * Requests to get the [voice region][Region] of this guild.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Region] wasn't present.
     * @throws [NoSuchElementException] if the [regionId] is not in the available [regions].
     */
    suspend fun getRegion(): Region = regions.first { it.id == regionId }

    /**
     * Requests to get the the channel in which a discoverable server's rules should be found represented
     *, returns null if the [GuildMessageChannel] isn't present, or [rulesChannelId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getRulesChannel(): GuildMessageChannel? = rulesChannel?.asChannel()

    /**
     * Gets the splash url in the specified [format], if present.
     */
    fun getSplashUrl(format: Image.Format): String? =
            data.splash?.let { "https://cdn.discordapp.com/splashes/${id.value}/$it.${format.extension}" }

    /**
     * Requests to get the splash image in the specified [format], if present.
     */
    suspend fun getSplash(format: Image.Format): Image? {
        val url = getSplashUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }

    /**
     * Requests to get the channel where system messages (member joins, server boosts, etc),
     * returns null if the [TextChannel] isn't present or the [systemChannelId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getSystemChannel(): TextChannel? =
            systemChannelId?.let { supplier.getChannelOfOrNull(it) }


    /**
     * Returns a new [Guild] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Guild = Guild(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Guild(data=$data, kord=$kord, supplier=$supplier)"
    }

}
