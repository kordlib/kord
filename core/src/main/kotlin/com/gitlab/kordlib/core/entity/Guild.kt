package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.DefaultMessageNotificationLevel
import com.gitlab.kordlib.common.entity.ExplicitContentFilter
import com.gitlab.kordlib.common.entity.MFALevel
import com.gitlab.kordlib.common.entity.VerificationLevel
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.Image
import com.gitlab.kordlib.core.`object`.data.EmojiData
import com.gitlab.kordlib.core.`object`.data.GuildData
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.TextChannelBehavior
import com.gitlab.kordlib.core.catchNotFound
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import com.gitlab.kordlib.core.switchIfEmpty
import com.gitlab.kordlib.core.toSnowflakeOrNull
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * An instance of a [Discord Guild](https://discordapp.com/developers/docs/resources/guild).
 */
@Suppress("MemberVisibilityCanBePrivate")
class Guild(val data: GuildData, override val kord: Kord) : GuildBehavior {

    override val id: Snowflake get() = Snowflake(data.id)

    /**
     * The id of the afk voice channel, if present.
     */
    val afkChannelId: Snowflake? get() = data.afkChannelId.toSnowflakeOrNull()

    /**
     * The afk timeout in seconds.
     */
    val afkTimeout: Int get() = data.afkTimeout

    /**
     *  The id of the guild creator if it is bot-created.
     */
    val applicationId: Snowflake? get() = data.applicationId.toSnowflakeOrNull()

    /**
     * The banner hash, if present.
     */
    val bannerHash: String? get() = data.banner

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
    val emojis: List<GuildEmoji> get() = data.emojis.map { GuildEmoji(it, id, kord) }

    /**
     * The behavior of the @everyone role.
     */
    val everyoneRole: RoleBehavior get() = RoleBehavior(id, id, kord)

    /**
     * The enabled guild features.
     */
    val features: Set<String> get() = data.features.toSet()

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
     * The required multi-factor authentication level of this guild.
     */
    val mfaLevel: MFALevel get() = data.mfaLevel


    override val members: Flow<Member>
        get() {
            if (data.members.isEmpty()) return super.members

            return data.members.asFlow().map { getMember(Snowflake(it.userId)) }.filterNotNull()
        }

    /**
     * The name of this guild.
     */
    val name: String get() = data.name

    override val channels: Flow<GuildChannel>
        get() = data.channels.asFlow().map { kord.getChannel(Snowflake(it)) }.filterIsInstance<GuildChannel>().switchIfEmpty(super.channels)

    /**
     * The default message notification level.
     */
    val defaultMessageNotificationLevel: DefaultMessageNotificationLevel get() = data.defaultMessageNotifications

    /**
     * The voice region id for the guild.
     */
    val regionId: Snowflake get() = Snowflake(data.region)

    /**
     * The splash hash, if present.
     */
    val splashHash: String? get() = data.splash

    /**
     * The id of the channel to which system messages are sent.
     */
    val systemChannelId: Snowflake? get() = data.systemChannelId.toSnowflakeOrNull()

    /**
     * The behavior of the channel to which system messages are sent.
     */
    val systemChannel: TextChannelBehavior? get() = systemChannelId?.let {
        TextChannelBehavior(guildId = id, id = it, kord = kord)
    }

    /**
     * The verification level required for the guild.
     */
    val verificationLevel: VerificationLevel get() = data.verificationLevel

    override val roles: Flow<Role>
        get() = roleIds.asFlow().map { kord.getRole(id, it)!! }

    /**
     * The ids of the roles.
     */
    val roleIds: Set<Snowflake> get() = data.roles.asSequence().map { Snowflake(it) }.toSet()

    override suspend fun asGuild(): Guild = this

    /**
     * Requests to get the afk channel, if present.
     */
    suspend fun getAfkChannel(): VoiceChannel? = afkChannelId?.let { kord.getChannel(it) as? VoiceChannel }

    /**
     * Gets the banner url in the specified format.
     */
    fun getBannerUrl(format: Image.Format): String? = data.banner?.let { "https://cdn.discordapp.com/banners/${id.value}/$it.${format.extension}" }

    /**
     * Requests to get the banner image in the specified [format], if present.
     */
    suspend fun getBanner(format: Image.Format): Image? = TODO()

    /**
     * Requests to get the embed channel, if present.
     */
    suspend fun getEmbedChannel(): GuildChannel? = embedChannelId?.let { kord.getChannel(it) } as? GuildChannel

    /**
     * Requests to get the emoji with given [emojiId], if present.
     */
    suspend fun getEmoji(emojiId: Snowflake): GuildEmoji? {
        val response = catchNotFound {
            kord.rest.emoji.getEmoji(guildId = id.value, emojiId = emojiId.value)
        } ?: return null

        val data = EmojiData.from(emojiId.value, response)

        return GuildEmoji(data, id, kord)
    }

    /**
     * Requests to get the @everyone role.
     */
    suspend fun getEveryoneRole(): Role = kord.getRole(id, id)!!

    /**
     * Gets the icon url, if present.
     */
    fun getIconUrl(format: Image.Format): String? = data.icon?.let { "https://cdn.discordapp.com/icons/${id.value}/$it.${format.extension}" }

    /**
     * Requests to get the icon image in the specified [format], if present.
     */
    suspend fun getIcon(format: Image.Format): Image? = TODO()

    /**
     * Requests to get the owner as member.
     */
    suspend fun getOwner(): Member = kord.getMember(id, ownerId)!!

    /**
     * Requests to get the voice region for this guild.
     */
    suspend fun getRegion(): Region = regions.first { it.id == regionId }


    /**
     * Gets the splash url in the specified [format].
     */
    fun getSplashUrl(format: Image.Format): String? =
            data.splash?.let { "https://cdn.discordapp.com/splashes/${id.value}/$it.${format.extension}" }

    /**
     * Requests to get the splash image in the specified [format], if present.
     */
    suspend fun getSplash(format: Image.Format): Image? = TODO()

    /**
     * Requests to get the channel of system messages, if present.
     */
    suspend fun getSystemChannel(): TextChannel? = kord.getChannel(id) as? TextChannel

}
