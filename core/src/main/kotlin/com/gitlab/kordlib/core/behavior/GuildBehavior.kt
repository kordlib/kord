package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.sorted
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy.Companion.rest
import com.gitlab.kordlib.rest.builder.ban.BanCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.*
import com.gitlab.kordlib.rest.builder.guild.EmojiCreateBuilder
import com.gitlab.kordlib.rest.builder.guild.EmojiModifyBuilder
import com.gitlab.kordlib.rest.builder.guild.GuildModifyBuilder
import com.gitlab.kordlib.rest.builder.role.RoleCreateBuilder
import com.gitlab.kordlib.rest.builder.role.RolePositionsModifyBuilder
import com.gitlab.kordlib.rest.json.request.CurrentUserNicknameModifyRequest
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.createCategory
import com.gitlab.kordlib.rest.service.createNewsChannel
import com.gitlab.kordlib.rest.service.createTextChannel
import com.gitlab.kordlib.rest.service.createVoiceChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.*
import com.gitlab.kordlib.rest.service.RestClient

/**
 * The behavior of a [Discord Guild](https://discordapp.com/developers/docs/resources/guild).
 */
interface GuildBehavior : Entity, Strategizable {
    /**
     * Requests to get all present bans for this guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val bans: Flow<Ban>
        get() = supplier.getGuildBans(id)

    /**
     * Requests to get all present webhooks for this guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val webhooks: Flow<Webhook>
        get() = supplier.getGuildWebhooks(id)

    /**
     * Requests to get all present channels in this guild in an unspecified order,
     * call [sorted] to get a consistent order.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val channels: Flow<GuildChannel>
        get() = supplier.getGuildChannels(id)

    /**
     * Requests to get the integrations of this guild.
     */
    val integrations: Flow<Integration>
        get() = flow {
            kord.rest.guild.getGuildIntegrations(id.value).forEach {
                emit(Integration(IntegrationData.from(id.longValue, it), kord, supplier))
            }
        }

    /**
     * Requests to get all present presences of this guild.
     *
     * This property is not resolvable through REST and will always use [KordCache] instead.
     */
    val presences: Flow<Presence>
        get() = kord.cache.query<PresenceData> { PresenceData::guildId eq id.longValue }
                .asFlow()
                .map { Presence(it, kord) }

    /**
     * Requests to get all present members in this guild.
     *
     * Unrestricted consumption of the returned [Flow] is a potentially performance intensive operation, it is thus recommended
     * to limit the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions that limit the amount
     * of messages requested.
     *
     * ```kotlin
     *  guild.members.first { it.displayName == targetName }
     * ```
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val members: Flow<Member>
        get() = supplier.getGuildMembers(id)

    /**
     * Requests to get the present voice regions for this guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val regions: Flow<Region>
        get() = supplier.getGuildVoiceRegions(id)

    /**
     * Requests to get all present roles in the guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val roles: Flow<Role>
        get() = supplier.getGuildRoles(id)

    /**
     * Requests to get the present voice states of this guild.
     *
     * This property is not resolvable through REST and will always use [KordCache] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val voiceStates: Flow<VoiceState>
        get() = kord.cache
                .query<VoiceStateData> { VoiceStateData::guildId eq id.longValue }
                .asFlow()
                .map { VoiceState(it, kord) }


    /**
     * Requests to get the this behavior as a [Guild].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    suspend fun asGuild(): Guild = supplier.getGuild(id)

    /**
     * Requests to get this behavior as a [Guild],
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun asGuildOrNull(): Guild? = supplier.getGuildOrNull(id)

    /**
     * Requests to delete this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete() = kord.rest.guild.deleteGuild(id.value)

    /**
     * Requests to leave this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun leave() = kord.rest.user.leaveGuild(id.value)

    /**
     * Requests to get the [Member] represented by the [userId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the member wasn't present.
     */
    suspend fun getMember(userId: Snowflake): Member = supplier.getMember(id, userId)

    /**
     * Requests to get the [Member] represented by the [userId],
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getMemberOrNull(userId: Snowflake): Member? = supplier.getMemberOrNull(id, userId)


    /**
     * Requests to get the [Role] represented by the [roleId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Role] wasn't present.
     */
    suspend fun getRole(roleId: Snowflake): Role = supplier.getRole(guildId = id, roleId = roleId)

    /**
     * Requests to get the [Role] represented by the [roleId],
     * returns null if the [Role] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getRoleOrNull(roleId: Snowflake): Role? = supplier.getRoleOrNull(guildId = id, roleId = roleId)

    /**
     * Requests to get the [Invite] represented by the [code].
     *
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Invite] wasn't present.
     */
    suspend fun getInvite(code: String, withCounts: Boolean = true): Invite =
            kord.with(rest).getInvite(code, withCounts)

    /**
     * Requests to get the [Invite] represented by the [code],
     * returns null if the [Invite] isn't present.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getInviteOrNull(code: String, withCounts: Boolean = true): Invite? =
            kord.with(rest).getInviteOrNull(code, withCounts)


    /**
     *  Requests to change the nickname of the bot in this guild, passing `null` will remove it.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun modifySelfNickname(newNickName: String?): String {
        return kord.rest.guild.modifyCurrentUserNickname(id.value, CurrentUserNicknameModifyRequest(newNickName))
    }

    /**
     * Requests to kick the given [userId].
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun kick(userId: Snowflake) {
        kord.rest.guild.deleteGuildMember(guildId = id.value, userId = userId.value)
    }


    /**
     * Requests to get the [Ban] of the [User] represented by the [userId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Ban] wasn't present.
     */
    suspend fun getBan(userId: Snowflake): Ban = supplier.getGuildBan(id, userId)

    /**
     * Requests to get the [Ban] of the [User] represented by the [userId],
     * returns null if the [Ban] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getBanOrNull(userId: Snowflake): Ban? = supplier.getGuildBanOrNull(id, userId)


    /**
     * Requests to unban the given [userId].
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun unBan(userId: Snowflake) {
        kord.rest.guild.deleteGuildBan(guildId = id.value, userId = userId.value)
    }

    /**
     * Returns the preview of this guild. The bot does not need to present in this guild
     * for this to complete successfully.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * @throws RequestException if the guild does not exist or is not public.
     * @throws [EntityNotFoundException] if the preview was not found.
     */
    suspend fun getPreview(): GuildPreview = kord.with(rest).getGuildPreview(id)

    /**
     * Returns the preview of this guild. The bot does not need to present in this guild
     * for this to complete successfully. Returns null if the preview doesn't exist.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * @throws RequestException if the guild does not exist or is not public.
     */
    suspend fun getPreviewOrNull(): GuildPreview? = kord.with(rest).getGuildPreviewOrNull(id)

    /**
     * Requests to get the amount of users that would be pruned in this guild.
     *
     * A user is pruned if they have not been seen within the given [days]
     * and don't have a [Role] assigned in this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun getPruneCount(days: Int = 7): Int =
            kord.rest.guild.getGuildPruneCount(id.value, days).pruned

    /**
     * Requests to prune users in this guild.
     *
     * A user is pruned if they have not been seen within the given [days]
     * and don't have a [Role] assigned in this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun prune(days: Int = 7): Int {
        return kord.rest.guild.beginGuildPrune(id.value, days, true).pruned!!
    }

    /**
     * Requests to get the vanity url of this guild, if present.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun getVanityUrl(): String? {
        val identifier = kord.rest.guild.getVanityInvite(id.value).code ?: return null
        return "https://discord.gg/$identifier"
    }

    /**
     * Returns a new [GuildBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildBehavior = GuildBehavior(id, kord, strategy)

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord, strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy) = object : GuildBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)

            override fun hashCode(): Int = Objects.hash(id)

            override fun equals(other: Any?): Boolean = when (other) {
                is GuildBehavior -> other.id == id
                is PartialGuild -> other.id == id
                else -> false
            }
        }
    }

}

/**
 * Requests to edit this guild.
 *
 * @return The edited [Guild].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun GuildBehavior.edit(builder: GuildModifyBuilder.() -> Unit): Guild {
    val response = kord.rest.guild.modifyGuild(id.value, builder)
    val data = GuildData.from(response)

    return Guild(data, kord)
}

suspend inline fun GuildBehavior.createEmoji(builder: EmojiCreateBuilder.() -> Unit) {
    kord.rest.emoji.createEmoji(guildId = id.value, builder = builder)
}

/**
 * Requests to create a new text channel.
 *
 * @return The created [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun GuildBehavior.createTextChannel(builder: TextChannelCreateBuilder.() -> Unit): TextChannel {
    val response = kord.rest.guild.createTextChannel(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as TextChannel
}

/**
 * Requests to create a new voice channel.
 *
 * @return The created [VoiceChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.createVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): VoiceChannel {
    val response = kord.rest.guild.createVoiceChannel(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as VoiceChannel
}

/**
 * Requests to create a new news channel.
 *
 * @return The created [NewsChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@KordPreview
suspend inline fun GuildBehavior.createNewsChannel(builder: NewsChannelCreateBuilder.() -> Unit): NewsChannel {
    val response = kord.rest.guild.createNewsChannel(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as NewsChannel
}

/**
 * Requests to create a new category.
 *
 * @return The created [Category].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun GuildBehavior.createCategory(builder: CategoryCreateBuilder.() -> Unit): Category {
    val response = kord.rest.guild.createCategory(id.value, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as Category
}

/**
 * Requests to swap positions of channels in this guild.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun GuildBehavior.swapChannelPositions(builder: GuildChannelPositionModifyBuilder.() -> Unit) {
    kord.rest.guild.modifyGuildChannelPosition(id.value, builder)
}

/**
 * Requests to swap positions of roles in this guild.
 *
 * This request will execute regardless of the consumption of the return value.
 *
 * @return the roles of this guild after the update in an unspecified order, call [sorted] to get a consistent order.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.swapRolePositions(builder: RolePositionsModifyBuilder.() -> Unit): Flow<Role> {
    val response = kord.rest.guild.modifyGuildRolePosition(id.value, builder)
    return response.asFlow().map { RoleData.from(id.value, it) }.map { Role(it, kord) }

}

/**
 * Requests to add a new role to this guild.
 *
 * @return The created [Role].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildBehavior.addRole(builder: RoleCreateBuilder.() -> Unit): Role {
    val response = kord.rest.guild.createGuildRole(id.value, builder)
    val data = RoleData.from(id.value, response)

    return Role(data, kord)
}

/**
 * Requests to ban the given [userId] in this guild.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun GuildBehavior.ban(userId: Snowflake, builder: BanCreateBuilder.() -> Unit) {
    kord.rest.guild.addGuildBan(guildId = id.value, userId = userId.value, builder = builder)
}