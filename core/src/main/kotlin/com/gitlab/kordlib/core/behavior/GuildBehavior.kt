package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import com.gitlab.kordlib.common.annotation.KordPreview
import com.gitlab.kordlib.common.entity.DiscordEmoji
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.*
import com.gitlab.kordlib.core.catchDiscordError
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.sorted
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy.Companion.rest
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import com.gitlab.kordlib.rest.builder.auditlog.AuditLogGetRequestBuilder
import com.gitlab.kordlib.rest.builder.ban.BanCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.*
import com.gitlab.kordlib.rest.builder.guild.EmojiCreateBuilder
import com.gitlab.kordlib.rest.builder.guild.GuildModifyBuilder
import com.gitlab.kordlib.rest.builder.guild.GuildWidgetModifyBuilder
import com.gitlab.kordlib.rest.builder.role.RoleCreateBuilder
import com.gitlab.kordlib.rest.builder.role.RolePositionsModifyBuilder
import com.gitlab.kordlib.rest.json.JsonErrorCode
import com.gitlab.kordlib.rest.json.request.CurrentUserNicknameModifyRequest
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Guild](https://discord.com/developers/docs/resources/guild).
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
     * Requests to get all custom emojis in this guild in an unspecified order.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val emojis: Flow<GuildEmoji>
        get() = supplier.getEmojis(id)

    /**
     * Requests to get the integrations of this guild.
     */
    val integrations: Flow<Integration>
        get() = flow {
            kord.rest.guild.getGuildIntegrations(id).forEach {
                emit(Integration(IntegrationData.from(id, it), kord, supplier))
            }
        }

    /**
     * Requests to get all present presences of this guild.
     *
     * This property is not resolvable through REST and will always use [KordCache] instead.
     */
    val presences: Flow<Presence>
        get() = kord.cache.query<PresenceData> { PresenceData::guildId eq id.value }
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
                .query<VoiceStateData> { VoiceStateData::guildId eq id.value }
                .asFlow()
                .map { VoiceState(it, kord) }
    /**
     * Requests to get the present voice states of this guild.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val invites: Flow<Invite>
        get() = flow {
            kord.rest.guild.getGuildInvites(id).forEach {
                emit(Invite(InviteData.from(it), kord))
            }
        }

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
    suspend fun delete() = kord.rest.guild.deleteGuild(id)

    /**
     * Requests to leave this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun leave() = kord.rest.user.leaveGuild(id)

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
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Use editSelfNickname.", ReplaceWith("editSelfNickname(newNickname)"), DeprecationLevel.ERROR)
    suspend fun modifySelfNickname(newNickname: String? = null): String {
        return kord.rest.guild.modifyCurrentUserNickname(id, CurrentUserNicknameModifyRequest(newNickname))
    }

    /**
     *  Requests to change the nickname of the bot in this guild, passing `null` will remove it.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun editSelfNickname(newNickname: String? = null): String {
        return kord.rest.guild.modifyCurrentUserNickname(id, CurrentUserNicknameModifyRequest(newNickname))
    }

    /**
     * Requests to kick the given [userId].
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun kick(userId: Snowflake, reason: String? = null) {
        kord.rest.guild.deleteGuildMember(guildId = id, userId = userId, reason = reason)
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
     * Requests to get the [GuildChannel] represented by the [channelId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [GuildChannel] wasn't present.
     * @throws [ClassCastException] if the channel is not a [GuildChannel].
     * @throws [IllegalArgumentException] if the channel is not part of this guild.
     */
    suspend fun getChannel(channelId: Snowflake): GuildChannel {
        val channel = supplier.getChannelOf<GuildChannel>(channelId)
        require(channel.guildId == this.id) { "channel ${channelId} is not in guild ${this.id}" }
        return channel
    }

    /**
     * Requests to get the [GuildChannel] represented by the [channelId],
     * returns null if the [GuildChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [ClassCastException] if the channel is not a [GuildChannel].
     * @throws [IllegalArgumentException] if the channel is not part of this guild.
     */
    suspend fun getChannelOrNull(channelId: Snowflake): GuildChannel? {
        val channel = supplier.getChannelOfOrNull<GuildChannel>(channelId) ?: return null
        require(channel.guildId == this.id) { "channel ${channelId} is not in guild ${this.id}" }
        return channel
    }

    /**
     * Requests to unban the given [userId].
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    @Deprecated("unBan is a typo", ReplaceWith("unban"))
    suspend fun unBan(userId: Snowflake) {
        kord.rest.guild.deleteGuildBan(guildId = id, userId = userId)
    }

    /**
     * Requests to unban the given [userId].
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun unban(userId: Snowflake) {
        kord.rest.guild.deleteGuildBan(guildId = id, userId = userId)
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
            kord.rest.guild.getGuildPruneCount(id, days).pruned

    /**
     * Requests to prune users in this guild.
     *
     * A user is pruned if they have not been seen within the given [days]
     * and don't have a [Role] assigned in this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun prune(days: Int = 7): Int {
        return kord.rest.guild.beginGuildPrune(id, days, true).pruned!!
    }

    /**
     * Requests to get the vanity url of this guild, if present.
     *
     * This function is not resolvable through cache and will always use the [RestClient] instead.
     * Request exceptions containing the [JsonErrorCode.InviteCodeInvalidOrTaken] reason will be transformed
     * into `null` instead.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun getVanityUrl(): String? {
        val identifier = catchDiscordError(JsonErrorCode.InviteCodeInvalidOrTaken) {
            kord.rest.guild.getVanityInvite(id).code
        } ?: return null
        return "https://discord.gg/$identifier"
    }

    suspend fun getWidget(): GuildWidget = supplier.getGuildWidget(id)

    suspend fun getWidgetOrNull(): GuildWidget? = supplier.getGuildWidget(id)

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

            override fun toString(): String {
                return "GuildBehavior(id=$id, kord=$kord, supplier$supplier)"
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
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.edit(builder: GuildModifyBuilder.() -> Unit): Guild {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.modifyGuild(id, builder)
    val data = GuildData.from(response)

    return Guild(data, kord)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createEmoji(builder: EmojiCreateBuilder.() -> Unit): GuildEmoji {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val discordEmoji = kord.rest.emoji.createEmoji(guildId = id, builder)
    return GuildEmoji(EmojiData.from(guildId = id, id = discordEmoji.id!!, discordEmoji), kord)
}

/**
 * Requests to create a new text channel.
 *
 * @return The created [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createTextChannel(builder: TextChannelCreateBuilder.() -> Unit): TextChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createTextChannel(id, builder)
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
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): VoiceChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createVoiceChannel(id, builder)
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
@OptIn(ExperimentalContracts::class)
@KordPreview
suspend inline fun GuildBehavior.createNewsChannel(builder: NewsChannelCreateBuilder.() -> Unit): NewsChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createNewsChannel(id, builder)
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
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createCategory(builder: CategoryCreateBuilder.() -> Unit): Category {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createCategory(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as Category
}

/**
 * Requests to swap positions of channels in this guild.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.swapChannelPositions(builder: GuildChannelPositionModifyBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.guild.modifyGuildChannelPosition(id, builder)
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
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.swapRolePositions(builder: RolePositionsModifyBuilder.() -> Unit): Flow<Role> {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.modifyGuildRolePosition(id, builder)
    return response.asFlow().map { RoleData.from(id, it) }.map { Role(it, kord) }

}

/**
 * Requests to add a new role to this guild.
 *
 * @return The created [Role].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
@DeprecatedSinceKord("0.7.0")
@Deprecated("Use createRole instead.", ReplaceWith("createRole(builder)"), DeprecationLevel.ERROR)
suspend inline fun GuildBehavior.addRole(builder: RoleCreateBuilder.() -> Unit = {}): Role = createRole(builder)

/**
 * Requests to add a new role to this guild.
 *
 * @return The created [Role].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createRole(builder: RoleCreateBuilder.() -> Unit = {}): Role {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createGuildRole(id, builder)
    val data = RoleData.from(id, response)

    return Role(data, kord)
}

/**
 * Requests to ban the given [userId] in this guild.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.ban(userId: Snowflake, builder: BanCreateBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.guild.addGuildBan(guildId = id, userId = userId, builder = builder)
}

/**
 * Requests to get the [GuildChannel] represented by the [channelId] as type [T].
 *
 * @throws [RequestException] if anything went wrong during the request.
 * @throws [EntityNotFoundException] if the [T] wasn't present.
 * @throws [ClassCastException] if the channel is not of type [T].
 * @throws [IllegalArgumentException] if the channel is not part of this guild.
 */
suspend inline fun <reified T : GuildChannel> GuildBehavior.getChannelOf(channelId: Snowflake): T {
    val channel = supplier.getChannelOf<T>(channelId)
    require(channel.guildId == this.id) { "channel ${channelId} is not in guild ${this.id}" }
    return channel
}

/**
 * Requests to get the [GuildChannel] represented by the [channelId] as type [T],
 * returns null if the [GuildChannel] isn't present.
 *
 * @throws [RequestException] if anything went wrong during the request.
 * @throws [ClassCastException] if the channel is not of type [T].
 * @throws [IllegalArgumentException] if the channel is not part of this guild.
 */
suspend inline fun <reified T : GuildChannel> GuildBehavior.getChannelOfOrNull(channelId: Snowflake): T? {
    val channel = supplier.getChannelOfOrNull<T>(channelId) ?: return null
    require(channel.guildId == this.id) { "channel ${channelId} is not in guild ${this.id}" }
    return channel
}

suspend inline fun GuildBehavior.editWidget(builder: GuildWidgetModifyBuilder.() -> Unit): GuildWidget {
    return GuildWidget(GuildWidgetData.from(kord.rest.guild.modifyGuildWidget(id, builder)), id, kord)
}

/**
 * The [Audit log entries][AuditLogEntry] from this guild, configured by the [builder].
 *
 * The returned flow is lazily executed, any [RequestException] will be thrown on
 * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
 *
 * ```kotlin
 *  val change = guild.getAuditLogEntries {
 *      userId = user.id
 *      action = AuditLogEvent.MemberUpdate
 *  }.mapNotNull { it[AuditLogChangeKey.Nick] }.firstOrNull() ?: return
 *
 *  println("user changed nickname from $old to $new")
 *  ```
 */
inline fun GuildBehavior.getAuditLogEntries(builder: AuditLogGetRequestBuilder.() -> Unit = {}): Flow<AuditLogEntry> =
        kord.with(rest).getAuditLogEntries(id, builder).map { AuditLogEntry(it, kord) }
