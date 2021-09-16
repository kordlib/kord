package dev.kord.core.entity

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.behavior.channel.GuildChannelBehavior
import dev.kord.core.behavior.channel.TopGuildChannelBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.behavior.channel.TopGuildMessageChannelBehavior
import dev.kord.core.behavior.channel.TextChannelBehavior
import dev.kord.core.behavior.channel.VoiceChannelBehavior
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.channel.*
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.Image
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import java.util.*

/**
 * An instance of a [Discord Guild](https://discord.com/developers/docs/resources/guild).
 */
class Guild(
    val data: GuildData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : GuildBehavior {

    override val id: Snowflake get() = data.id

    /**
     * The id of the afk voice channel, if present.
     */
    val afkChannelId: Snowflake? get() = data.afkChannelId

    val afkChannel: VoiceChannelBehavior?
        get() = afkChannelId?.let { VoiceChannelBehavior(guildId = id, id = it, kord = kord) }

    val threads: Flow<ThreadChannel>
        get() = flow {
            data.threads.mapList {
               val channel =  Channel.from(it, kord)
               if(channel is ThreadChannel) emit(channel)
            }
        }

    /**
     * The afk timeout in seconds.
     */
    val afkTimeout: Int get() = data.afkTimeout

    /**
     *  The id of the guild creator if it is bot-created.
     */
    val applicationId: Snowflake? get() = data.applicationId

    /**
     * The approximate number of members in this guild. Present if this guild was requested through
     * [rest][dev.kord.rest.service.RestClient] with the flag `with_counts`.
     */
    val approximateMemberCount: Int? get() = data.approximateMemberCount.value

    /**
     * The approximate number of online members in this guild. Present if this guild was requested through
     * [rest][dev.kord.rest.service.RestClient] with the flag `with_counts`.
     */
    val approximatePresenceCount: Int? get() = data.approximatePresenceCount.value

    /**
     * The maximum number of presences for this guild. 25000 By default.
     */
    val maxPresences: Int get() = data.maxPresences.orElse(25_000)

    /**
     * The maximum number of members for this guild.
     */
    val maxMembers: Int? get() = data.maxMembers.value

    /**
     * Total permissions of the bot in the Guild (excludes channel overrides).
     *
     * This field is only present if this guild was fetched through [Kord.guilds] with a
     * [EntitySupplyStrategy.rest].
     */
    val permissions: Permissions? get() = data.permissions.value

    /**
     * The server boost level.
     */
    val premiumTier: PremiumTier get() = data.premiumTier

    /**
     * The number of boosts this guild has, if present.
     */
    val premiumSubscriptionCount: Int? get() = data.premiumSubscriptionCount.value

    /**
     * The banner hash, if present.
     */
    val bannerHash: String? get() = data.banner

    /**
     * The ids of all [channels][TopGuildChannel].
     */
    val channelIds: Set<Snowflake> get() = data.channels.orEmpty().toSet()

    /**
     * The explicit content filter level.
     */
    val contentFilter: ExplicitContentFilter get() = data.explicitContentFilter

    /**
     * The description of this guild, if present.
     */
    val description: String? get() = data.description

    /**
     * Whether this guild has enabled its widget.
     */
    val isWidgetEnabled: Boolean get() = data.widgetEnabled.discordBoolean

    /**
     * The channel id that the guild's widget will generate an invite to, if set and enabled.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated(
        "Embed was renamed to widget.",
        ReplaceWith("widgetChannelId"),
        DeprecationLevel.ERROR
    )
    val embedChannelId: Snowflake? by ::widgetChannelId

    /**
     * The ID of the channel the widget will redirect users to, if present.
     */
    val widgetChannelId: Snowflake? get() = data.widgetChannelId.value

    /**
     * The behavior of the embedded channel, if present.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated(
        "Embed was renamed to widget.",
        ReplaceWith("widgetChannelId"),
        DeprecationLevel.ERROR
    )
    val embedChannel: TopGuildChannelBehavior? by ::widgetChannel

    /**
     * The behavior of the channel widgets will redirect users to, if present.
     */
    val widgetChannel: TopGuildChannelBehavior?
        get() = widgetChannelId?.let { TopGuildChannelBehavior(guildId = id, id = it, kord = kord) }

    /**
     * The ids of custom emojis in this guild.
     */
    val emojiIds: Set<Snowflake> get() = data.emojis.toSet()

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
    val joinedTime: Instant?
        get() = data.joinedAt.value?.toInstant()

    /**
     * The id of the owner.
     */
    val ownerId: Snowflake get() = data.ownerId

    /**
     * Whether this bot is the owner of the server
     */
    val isOwner: Boolean get() = ownerId == kord.selfId

    /**
     * True if the guild is considered large, if present.
     *
     * This field is only present on Guilds created through [dev.kord.core.event.guild.GuildCreateEvent].
     */
    val isLarge: Boolean? get() = data.large.value

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
    val memberCount: Int? get() = data.memberCount.value

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
    val publicUpdatesChannelId: Snowflake? get() = data.publicUpdatesChannelId

    /**
     * The behavior of the channel where guild notices such as welcome messages and boost events are posted.
     */
    val publicUpdatesChannel: TopGuildMessageChannelBehavior?
        get() = publicUpdatesChannelId?.let {
            TopGuildMessageChannelBehavior(guildId = id, id = it, kord = kord)
        }

    val preferredLocale: Locale get() = Locale.forLanguageTag(data.preferredLocale)

    /**
     * The behaviors of all [channels][TopGuildChannel].
     */
    val channelBehaviors: Set<TopGuildChannelBehavior>
        get() = data.channels.orEmpty().asSequence().map { TopGuildChannelBehavior(id = it, guildId = id, kord = kord) }
            .toSet()

    /**
     * The default message notification level.
     */
    val defaultMessageNotificationLevel: DefaultMessageNotificationLevel get() = data.defaultMessageNotifications

    /**
     * The id of the channel in which a discoverable server's rules should be found
     **/
    val rulesChannelId: Snowflake? get() = data.rulesChannelId

    /**
     * The channel behavior in which a discoverable server's rules should be found.
     **/
    val rulesChannel: TopGuildMessageChannelBehavior?
        get() = data.rulesChannelId?.let {
            TopGuildMessageChannelBehavior(id, it, kord)
        }

    /**
     * The splash hash, if present.
     */
    val splashHash: String? get() = data.splash.value

    /**
     * The hash of the discovery splash, if present.
     */
    val discoverySplashHash: String? get() = data.discoverySplash.value

    /**
     * The id of the channel to which system messages are sent.
     */
    val systemChannelId: Snowflake? get() = data.systemChannelId

    /**
     * The behavior of the channel to which system messages are sent.
     */
    val systemChannel: TextChannelBehavior?
        get() = systemChannelId?.let {
            TextChannelBehavior(guildId = id, id = it, kord = kord)
        }

    val systemChannelFlags: SystemChannelFlags get() = data.systemChannelFlags

    /**
     * The verification level required for the guild.
     */
    val verificationLevel: VerificationLevel get() = data.verificationLevel

    /**
     * The ids of the roles.
     */
    val roleIds: Set<Snowflake> get() = data.roles.asSequence().map { it }.toSet()

    /**
     * The behaviors of the [roles][Role].
     */
    val roleBehaviors: Set<RoleBehavior>
        get() = data.roles.asSequence().map { RoleBehavior(id = it, guildId = id, kord = kord) }.toSet()

    /**
     * The vanity code of this server used in the [vanityUrl], if present.
     */
    val vanityCode: String? get() = data.vanityUrlCode

    /**
     * The vanity invite URL of this server, if present.
     */
    val vanityUrl: String? get() = data.vanityUrlCode?.let { "https://discord.gg/$it" }


    /**
     * The maximum amount of users in a video channel, if present.
     */
    val maxVideoChannelUsers: Int? get() = data.maxVideoChannelUsers.value

    /**
     * The welcome screen of a Community guild, shown to new members, returned in an [Invite]'s guild object
     */
    val welcomeScreen: WelcomeScreen? get() = data.welcomeScreen.unwrap { WelcomeScreen(it, kord) }

    /**
     * The [NSFW Level](https://discord.com/developers/docs/resources/guild#guild-object-guild-nsfw-level) of this Guild
     */
    val nsfw: NsfwLevel get() = data.nsfwLevel

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
    fun getBannerUrl(format: Image.Format): String? =
        data.banner?.let { "https://cdn.discordapp.com/banners/${id.asString}/$it.${format.extension}" }

    /**
     * Requests to get the banner image in the specified [format], if present.
     */
    suspend fun getBanner(format: Image.Format): Image? {
        val url = getBannerUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }

    /**
     * Requests to get the [TopGuildChannel] represented by the [embedChannel],
     * returns null if the [TopGuildChannel] isn't present or [embedChannel] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getEmbedChannel(): TopGuildChannel? = widgetChannelId?.let { supplier.getChannelOfOrNull(it) }

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
        data.splash.value?.let { "discovery-splashes/${id.asString}/${it}.${format.extension}" }

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
    fun getIconUrl(format: Image.Format): String? =
        data.icon?.let { "https://cdn.discordapp.com/icons/${id.asString}/$it.${format.extension}" }

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
     * Requests to get the owner of this guild as a [Member],
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getOwnerOrNull(): Member? = supplier.getMemberOrNull(id, ownerId)

    /**
     * Requests to get The channel where guild notices such as welcome messages and boost events are posted.
     */
    suspend fun getPublicUpdatesChannel(): TopGuildMessageChannel? = publicUpdatesChannel?.asChannel()

    /**
     * Requests to get the the channel in which a discoverable server's rules should be found represented
     *, returns null if the [TopGuildMessageChannel] isn't present, or [rulesChannelId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getRulesChannel(): TopGuildMessageChannel? = rulesChannel?.asChannel()

    /**
     * Gets the splash url in the specified [format], if present.
     */
    fun getSplashUrl(format: Image.Format): String? =
        data.splash.value?.let { "https://cdn.discordapp.com/splashes/${id.asString}/$it.${format.extension}" }

    /**
     * Requests to get the splash image in the specified [format], if present.
     */
    suspend fun getSplash(format: Image.Format): Image? {
        val url = getSplashUrl(format) ?: return null

        return Image.fromUrl(kord.resources.httpClient, url)
    }

    /**
     * Requests to get the channel where system messages (member joins, server boosts, etc) are sent,
     * returns null if the [TextChannel] isn't present or the [systemChannelId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getSystemChannel(): TextChannel? =
        systemChannelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Requests to get the channel the widget will redirect users to,
     * returns null if the [TextChannel] isn't present or the [widgetChannelId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getWidgetChannel(): TopGuildMessageChannel? =
        widgetChannelId?.let { supplier.getChannelOfOrNull(it) }

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
