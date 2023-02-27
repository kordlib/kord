package dev.kord.core.behavior

import dev.kord.cache.api.query
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.entity.*
import dev.kord.common.entity.AutoModerationRuleEventType.MessageSend
import dev.kord.common.entity.Permission.ManageGuild
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.*
import dev.kord.core.cache.idEq
import dev.kord.core.catchDiscordError
import dev.kord.core.entity.*
import dev.kord.core.entity.application.GuildApplicationCommand
import dev.kord.core.entity.application.GuildChatInputCommand
import dev.kord.core.entity.application.GuildMessageCommand
import dev.kord.core.entity.application.GuildUserCommand
import dev.kord.core.entity.automoderation.*
import dev.kord.core.entity.channel.*
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.event.guild.MembersChunkEvent
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.hash
import dev.kord.core.supplier.*
import dev.kord.core.supplier.EntitySupplyStrategy.Companion.rest
import dev.kord.gateway.Gateway
import dev.kord.gateway.PrivilegedIntent
import dev.kord.gateway.RequestGuildMembers
import dev.kord.gateway.builder.RequestGuildMembersBuilder
import dev.kord.gateway.start
import dev.kord.rest.Image
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.auditlog.AuditLogGetRequestBuilder
import dev.kord.rest.builder.automoderation.KeywordAutoModerationRuleCreateBuilder
import dev.kord.rest.builder.automoderation.KeywordPresetAutoModerationRuleCreateBuilder
import dev.kord.rest.builder.automoderation.MentionSpamAutoModerationRuleCreateBuilder
import dev.kord.rest.builder.automoderation.SpamAutoModerationRuleCreateBuilder
import dev.kord.rest.builder.ban.BanCreateBuilder
import dev.kord.rest.builder.channel.*
import dev.kord.rest.builder.guild.*
import dev.kord.rest.builder.interaction.ChatInputCreateBuilder
import dev.kord.rest.builder.interaction.GuildMultiApplicationCommandBuilder
import dev.kord.rest.builder.interaction.MessageCommandCreateBuilder
import dev.kord.rest.builder.interaction.UserCommandCreateBuilder
import dev.kord.rest.builder.role.RoleCreateBuilder
import dev.kord.rest.builder.role.RolePositionsModifyBuilder
import dev.kord.rest.json.JsonErrorCode
import dev.kord.rest.json.request.CurrentUserNicknameModifyRequest
import dev.kord.rest.json.request.GuildStickerCreateRequest
import dev.kord.rest.json.request.MultipartGuildStickerCreateRequest
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Guild](https://discord.com/developers/docs/resources/guild).
 */
public interface GuildBehavior : KordEntity, Strategizable {
    /**
     * Requests to get all present bans for this guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val bans: Flow<Ban>
        get() = supplier.getGuildBans(id)

    /**
     * Returns all active public and private threads in this guild
     * Threads are ordered by their id, in descending order.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val activeThreads: Flow<ThreadChannel>
        get() = supplier.getActiveThreads(id)

    /**
     * Requests to get all threads in this guild that are present in [cache][Kord.cache].
     *
     * This property is not resolvable through REST and will always use [Kord.cache] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val cachedThreads: Flow<ThreadChannel>
        get() = kord.cache
            .query<ChannelData> { idEq(ChannelData::guildId, this@GuildBehavior.id) }
            .asFlow()
            .mapNotNull { Channel.from(it, kord) as? ThreadChannel }

    /**
     * Requests to get all present webhooks for this guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val webhooks: Flow<Webhook>
        get() = supplier.getGuildWebhooks(id)

    /**
     * Requests to get all present channels in this guild in an unspecified order,
     * call [toList()][toList].[sorted()][sorted] on the returned [Flow] to get a consistent order.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val channels: Flow<TopGuildChannel>
        get() = supplier.getGuildChannels(id)

    /**
     * Requests to get all custom emojis in this guild in an unspecified order.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val emojis: Flow<GuildEmoji>
        get() = supplier.getEmojis(id)

    /**
     * Requests to get the integrations of this guild.
     */
    public val integrations: Flow<Integration>
        get() = flow {
            kord.rest.guild.getGuildIntegrations(id).forEach {
                emit(Integration(IntegrationData.from(id, it), kord, supplier))
            }
        }

    /**
     * Requests to get all present presences of this guild.
     *
     * This property is not resolvable through REST and will always use [Kord.cache] instead.
     */
    public val presences: Flow<Presence>
        get() = kord.cache.query<PresenceData> { idEq(PresenceData::guildId, id) }
            .asFlow()
            .map { Presence(it, kord) }

    /**
     * Requests to get all present members in this guild.
     *
     * Unrestricted consumption of the returned [Flow] is a potentially performance-intensive operation, it is thus
     * recommended limiting the amount of messages requested by using [Flow.take], [Flow.takeWhile] or other functions
     * that limit the amount of messages requested.
     *
     * ```kotlin
     *  guild.members.first { it.displayName == targetName }
     * ```
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val members: Flow<Member>
        get() = supplier.getGuildMembers(id)

    public val stickers: Flow<GuildSticker>
        get() = supplier.getGuildStickers(id)

    /**
     * Requests to get the present voice regions for this guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val regions: Flow<Region>
        get() = supplier.getGuildVoiceRegions(id)

    /**
     * Requests to get all present roles in the guild.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val roles: Flow<Role>
        get() = supplier.getGuildRoles(id)

    /**
     * Requests to get the present voice states of this guild.
     *
     * This property is not resolvable through REST and will always use [Kord.cache] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val voiceStates: Flow<VoiceState>
        get() = kord.cache
            .query<VoiceStateData> { idEq(VoiceStateData::guildId, id) }
            .asFlow()
            .map { VoiceState(it, kord) }

    /**
     * Requests to get the [invites][InviteWithMetadata] for this guild.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val invites: Flow<InviteWithMetadata>
        get() = flow {
            kord.rest.guild.getGuildInvites(id).forEach {
                val data = InviteWithMetadataData.from(it)
                emit(InviteWithMetadata(data, kord))
            }
        }

    public val templates: Flow<Template>
        get() = supplier.getTemplates(id)

    /**
     * Returns the gateway this guild is part of as per the
     * [Discord sharding rules](https://discord.com/developers/docs/topics/gateway#sharding).
     * Returns `null` if no gateway for the shard is present in [kord].
     */
    public val gateway: Gateway?
        get() {
            val shard = id.value.shr(22).toLong() % kord.resources.shards.totalShards.coerceAtLeast(1)
            return kord.gateway.gateways[shard.toInt()]
        }

    public val scheduledEvents: Flow<GuildScheduledEvent>
        get() = supplier.getGuildScheduledEvents(id)

    /**
     * Executes the [request] on this gateway, returning a flow of [MembersChunkEvent] responses.
     *
     * The returned flow is cold, and will execute the [request] only on subscription.
     * Collection of this flow on a [Gateway] that is not [running][Gateway.start]
     * will result in an [IllegalStateException] being thrown.
     *
     * Executing the [request] on a [Gateway] with a [Shard][dev.kord.common.entity.DiscordShard] that
     * [does not match the guild id](https://discord.com/developers/docs/topics/gateway#sharding)
     * can result in undefined behavior for the returned flow and inconsistencies in the cache.
     *
     * This function expects [request.nonce][RequestGuildMembers.nonce] to contain a value, but it is not required.
     * If no nonce was provided one will be generated instead.
     */
    @PrivilegedIntent
    public fun requestMembers(request: RequestGuildMembers): Flow<MembersChunkEvent> {
        val gateway = gateway ?: return emptyFlow()

        val nonce = request.nonce.value ?: RequestGuildMembers.Nonce.new()
        val withNonce = request.copy(nonce = Optional.Value(nonce))

        return kord.events
            .onSubscription { gateway.send(withNonce) }
            .filterIsInstance<MembersChunkEvent>()
            .filter { it.nonce == nonce }
            .transformWhile {
                emit(it)
                return@transformWhile (it.chunkIndex + 1) < it.chunkCount
            }
    }

    public fun getApplicationCommands(withLocalizations: Boolean? = null): Flow<GuildApplicationCommand> =
        supplier.getGuildApplicationCommands(kord.resources.applicationId, id, withLocalizations)

    public suspend fun getApplicationCommand(commandId: Snowflake): GuildApplicationCommand =
        supplier.getGuildApplicationCommand(kord.resources.applicationId, id, commandId)

    public suspend fun getApplicationCommandOrNull(commandId: Snowflake): GuildApplicationCommand? =
        supplier.getGuildApplicationCommandOrNull(kord.resources.applicationId, id, commandId)

    /**
     * Requests to get this behavior as a [Guild].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    public suspend fun asGuild(): Guild = supplier.getGuild(id)

    /**
     * Requests to get this behavior as a [Guild],
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun asGuildOrNull(): Guild? = supplier.getGuildOrNull(id)

    /**
     * Retrieve the [Guild] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    public suspend fun fetchGuild(): Guild = supplier.getGuild(id)


    /**
     * Retrieve the [Guild] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun fetchGuildOrNull(): Guild? = supplier.getGuildOrNull(id)

    /**
     * Requests to delete this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete(): Unit = kord.rest.guild.deleteGuild(id)

    /**
     * Requests to edit this guild's [MFA level][MFALevel] and returns the updated level.
     *
     * This requires guild ownership.
     *
     * @param reason the reason showing up in the audit log
     * @throws RestRequestException if something went wrong during the request.
     */
    public suspend fun editMFALevel(level: MFALevel, reason: String? = null): MFALevel =
        kord.rest.guild.modifyGuildMFALevel(id, level, reason).level

    /**
     * Requests to leave this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun leave(): Unit = kord.rest.user.leaveGuild(id)

    /**
     * Requests to get the [Member] represented by the [userId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the member wasn't present.
     */
    public suspend fun getMember(userId: Snowflake): Member = supplier.getMember(id, userId)

    /**
     * Requests to get up to [limit] members whose [Member.username] or [Member.nickname] match the [query].
     * The [limit] accepts a maximum value of `1000` and a minimum of `1`.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    @KordExperimental
    public fun getMembers(query: String, limit: Int = 1000): Flow<Member> = flow {
        kord.rest.guild.getGuildMembers(id, query, limit).forEach {
            emit(
                Member(
                    MemberData.from(userId = it.user.unwrap(DiscordUser::id)!!, guildId = id, it),
                    UserData.from(it.user.value!!),
                    kord
                )
            )
        }
    }

    /**
     * Requests to get the [Member] represented by the [userId],
     * returns null if the [Member] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getMemberOrNull(userId: Snowflake): Member? = supplier.getMemberOrNull(id, userId)


    /**
     * Requests to get the [Role] represented by the [roleId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Role] wasn't present.
     */
    public suspend fun getRole(roleId: Snowflake): Role = supplier.getRole(guildId = id, roleId = roleId)

    /**
     * Requests to get the [Role] represented by the [roleId],
     * returns null if the [Role] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getRoleOrNull(roleId: Snowflake): Role? = supplier.getRoleOrNull(guildId = id, roleId = roleId)

    /**
     * Requests to get the [Invite] represented by the [code].
     *
     * This is not resolvable through cache and will always use the [rest strategy][EntitySupplyStrategy.rest] instead.
     *
     * @throws RestRequestException if anything went wrong during the request.
     * @throws EntityNotFoundException if the [Invite] wasn't present.
     */
    public suspend fun getInvite(
        code: String,
        withCounts: Boolean = true,
        withExpiration: Boolean = true,
        scheduledEventId: Snowflake? = null,
    ): Invite = kord.with(rest).getInvite(code, withCounts, withExpiration, scheduledEventId)

    /**
     * Requests to get the [Invite] represented by the [code],
     * returns null if the [Invite] isn't present.
     *
     * This is not resolvable through cache and will always use the [rest strategy][EntitySupplyStrategy.rest] instead.
     *
     * @throws RestRequestException if anything went wrong during the request.
     */
    public suspend fun getInviteOrNull(
        code: String,
        withCounts: Boolean = true,
        withExpiration: Boolean = true,
        scheduledEventId: Snowflake? = null,
    ): Invite? = kord.with(rest).getInviteOrNull(code, withCounts, withExpiration, scheduledEventId)

    /**
     *  Requests to change the nickname of the bot in this guild, passing `null` will remove it.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun editSelfNickname(newNickname: String? = null, reason: String? = null): String {
        return kord.rest.guild.modifyCurrentUserNickname(
            id,
            CurrentUserNicknameModifyRequest(Optional(newNickname)),
            reason
        ).nick
    }

    /**
     * Requests to kick the given [userId].
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun kick(userId: Snowflake, reason: String? = null) {
        kord.rest.guild.deleteGuildMember(guildId = id, userId = userId, reason = reason)
    }


    /**
     * Requests to get the [Ban] of the [User] represented by the [userId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Ban] wasn't present.
     */
    public suspend fun getBan(userId: Snowflake): Ban = supplier.getGuildBan(id, userId)

    /**
     * Requests to get the [Ban] of the [User] represented by the [userId],
     * returns null if the [Ban] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getBanOrNull(userId: Snowflake): Ban? = supplier.getGuildBanOrNull(id, userId)

    /**
     * Requests to get the [TopGuildChannel] represented by the [channelId].
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [TopGuildChannel] wasn't present.
     * @throws [ClassCastException] if the channel is not a [TopGuildChannel].
     * @throws [IllegalArgumentException] if the channel is not part of this guild.
     */
    public suspend fun getChannel(channelId: Snowflake): GuildChannel {
        val channel = supplier.getChannelOf<GuildChannel>(channelId)
        require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
        return channel
    }

    /**
     * Requests to get the [GuildChannel] represented by the [channelId],
     * returns null if the [GuildChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [ClassCastException] if the channel is not a [TopGuildChannel].
     * @throws [IllegalArgumentException] if the channel is not part of this guild.
     */
    public suspend fun getChannelOrNull(channelId: Snowflake): GuildChannel? {
        val channel = supplier.getChannelOfOrNull<GuildChannel>(channelId) ?: return null
        require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
        return channel
    }

    /**
     * Requests to unban the given [userId].
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun unban(userId: Snowflake, reason: String? = null) {
        kord.rest.guild.deleteGuildBan(guildId = id, userId = userId, reason = reason)
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
    public suspend fun getPreview(): GuildPreview = kord.with(rest).getGuildPreview(id)

    /**
     * Returns the preview of this guild. The bot does not need to present in this guild
     * for this to complete successfully. Returns null if the preview doesn't exist.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * @throws RequestException if the guild does not exist or is not public.
     */
    public suspend fun getPreviewOrNull(): GuildPreview? = kord.with(rest).getGuildPreviewOrNull(id)

    /**
     * Requests to get the amount of users that would be pruned in this guild.
     *
     * A user is pruned if they have not been seen within the given [days]
     * and don't have a [Role] assigned in this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun getPruneCount(days: Int = 7): Int =
        kord.rest.guild.getGuildPruneCount(id, days).pruned

    /**
     * Requests to prune users in this guild.
     *
     * A user is pruned if they have not been seen within the given [days]
     * and don't have a [Role] assigned in this guild.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun prune(days: Int = 7, reason: String? = null): Int {
        return kord.rest.guild.beginGuildPrune(id, days, true, reason).pruned!!
    }

    public suspend fun getWelcomeScreenOrNull(): WelcomeScreen? =
        rest.supply(kord).getGuildWelcomeScreenOrNull(id)

    public suspend fun getWelcomeScreen(): WelcomeScreen =
        rest.supply(kord).getGuildWelcomeScreen(id)

    public suspend fun editWelcomeScreen(builder: WelcomeScreenModifyBuilder.() -> Unit): WelcomeScreen {
        val request = kord.rest.guild.modifyGuildWelcomeScreen(id, builder)
        val data = WelcomeScreenData.from(request)
        return WelcomeScreen(data, kord)
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
    public suspend fun getVanityUrl(): String? {
        val identifier = catchDiscordError(JsonErrorCode.InviteCodeInvalidOrTaken, JsonErrorCode.MissingAccess) {
            kord.rest.guild.getVanityInvite(id).code
        } ?: return null
        return "https://discord.gg/$identifier"
    }


    /**
     * Requests a [GuildScheduledEvent] by its [id].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildScheduledEvent(eventId: Snowflake): GuildScheduledEvent =
        supplier.getGuildScheduledEvent(id, eventId)

    /**
     * Requests a [GuildScheduledEvent] by its [id] returns `null` if none could be found.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildScheduledEventOrNull(eventId: Snowflake): GuildScheduledEvent? =
        supplier.getGuildScheduledEventOrNull(id, eventId)

    public suspend fun getWidget(): GuildWidget = supplier.getGuildWidget(id)

    public suspend fun getWidgetOrNull(): GuildWidget? = supplier.getGuildWidgetOrNull(id)

    public suspend fun getTemplate(code: String): Template = supplier.getTemplate(code)

    public suspend fun getTemplateOrNull(code: String): Template? = supplier.getTemplateOrNull(code)

    public suspend fun getSticker(stickerId: Snowflake): GuildSticker = supplier.getGuildSticker(id, stickerId)

    public suspend fun getStickerOrNull(stickerId: Snowflake): GuildSticker? =
        supplier.getGuildStickerOrNull(id, stickerId)

    public suspend fun createSticker(name: String, description: String, tags: String, file: NamedFile): GuildSticker {
        val request = MultipartGuildStickerCreateRequest(GuildStickerCreateRequest(name, description, tags), file)
        val response = kord.rest.sticker.createGuildSticker(id, request)
        val data = StickerData.from(response)
        return GuildSticker(data, kord)
    }

    /**
     * Requests to get all [AutoModerationRule]s currently configured for this guild.
     *
     * This requires the [ManageGuild] permission.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val autoModerationRules: Flow<AutoModerationRule>
        get() = supplier.getAutoModerationRules(guildId = id)

    /**
     * Requests an [AutoModerationRule] by its [id][ruleId]. Returns `null` if it wasn't found.
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if something went wrong during the request.
     */
    public suspend fun getAutoModerationRuleOrNull(ruleId: Snowflake): AutoModerationRule? =
        supplier.getAutoModerationRuleOrNull(guildId = id, ruleId)

    /**
     * Requests an [AutoModerationRule] by its [id][ruleId].
     *
     * This requires the [ManageGuild] permission.
     *
     * @throws RequestException if something went wrong during the request.
     * @throws EntityNotFoundException if the [AutoModerationRule] wasn't found.
     */
    public suspend fun getAutoModerationRule(ruleId: Snowflake): AutoModerationRule =
        supplier.getAutoModerationRule(guildId = id, ruleId)


    /**
     * Returns a new [GuildBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildBehavior = GuildBehavior(id, kord, strategy)
}

public suspend inline fun <reified T : GuildApplicationCommand> GuildBehavior.getApplicationCommandOfOrNull(commandId: Snowflake): T? {
    return supplier.getGuildApplicationCommandOfOrNull(kord.resources.applicationId, id, commandId)
}


public suspend inline fun <reified T : GuildApplicationCommand> GuildBehavior.getApplicationCommandOf(commandId: Snowflake): T {
    return supplier.getGuildApplicationCommandOf(kord.resources.applicationId, id, commandId)
}


public fun GuildBehavior(
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
): GuildBehavior = object : GuildBehavior {
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "GuildBehavior(id=$id, kord=$kord, supplier=$supplier)"
    }
}


public suspend inline fun GuildBehavior.createChatInputCommand(
    name: String,
    description: String,
    builder: ChatInputCreateBuilder.() -> Unit = {},
): GuildChatInputCommand {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return kord.createGuildChatInputCommand(id, name, description, builder)
}


public suspend inline fun GuildBehavior.createMessageCommand(
    name: String,
    builder: MessageCommandCreateBuilder.() -> Unit = {},
): GuildMessageCommand {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return kord.createGuildMessageCommand(id, name, builder)
}


public suspend inline fun GuildBehavior.createUserCommand(
    name: String,
    builder: UserCommandCreateBuilder.() -> Unit = {},
): GuildUserCommand {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return kord.createGuildUserCommand(id, name, builder)
}


public suspend inline fun GuildBehavior.createApplicationCommands(
    builder: GuildMultiApplicationCommandBuilder.() -> Unit
): Flow<GuildApplicationCommand> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return kord.createGuildApplicationCommands(id, builder)
}

/**
 * Requests to edit this guild.
 *
 * @return The edited [Guild].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.edit(builder: GuildModifyBuilder.() -> Unit): Guild {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.modifyGuild(id, builder)
    val data = GuildData.from(response)

    return Guild(data, kord)
}

public suspend inline fun GuildBehavior.createEmoji(
    name: String,
    image: Image,
    builder: EmojiCreateBuilder.() -> Unit = {}
): GuildEmoji {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val discordEmoji = kord.rest.emoji.createEmoji(guildId = id, name, image, builder)
    return GuildEmoji(EmojiData.from(guildId = id, id = discordEmoji.id!!, discordEmoji), kord)
}

/**
 * Requests to create a new text channel.
 *
 * @return The created [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */

public suspend inline fun GuildBehavior.createTextChannel(
    name: String,
    builder: TextChannelCreateBuilder.() -> Unit = {}
): TextChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createTextChannel(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as TextChannel
}

public suspend inline fun GuildBehavior.createForumChannel(
    name: String,
    builder: ForumChannelCreateBuilder.() -> Unit = {}
): ForumChannel  {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.guild.createForumChannel(id, name, builder)
    val data = ChannelData.from(response)
    return Channel.from(data, kord) as ForumChannel
}

/**
 * Requests to create a new voice channel.
 *
 * @return The created [VoiceChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.createVoiceChannel(
    name: String,
    builder: VoiceChannelCreateBuilder.() -> Unit = {}
): VoiceChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createVoiceChannel(id, name, builder)
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
public suspend inline fun GuildBehavior.createNewsChannel(
    name: String,
    builder: NewsChannelCreateBuilder.() -> Unit = {}
): NewsChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createNewsChannel(id, name, builder)
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
public suspend inline fun GuildBehavior.createCategory(
    name: String,
    builder: CategoryCreateBuilder.() -> Unit = {}
): Category {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val response = kord.rest.guild.createCategory(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as Category
}

/**
 * Requests to swap positions of channels in this guild.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.swapChannelPositions(builder: GuildChannelPositionModifyBuilder.() -> Unit) {
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
 * @return the roles of this guild after the update in an unspecified order, call [toList()][toList].[sorted()][sorted]
 * on the returned [Flow] to get a consistent order.
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.swapRolePositions(builder: RolePositionsModifyBuilder.() -> Unit): Flow<Role> {
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
public suspend inline fun GuildBehavior.createRole(builder: RoleCreateBuilder.() -> Unit = {}): Role {
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
public suspend inline fun GuildBehavior.ban(userId: Snowflake, builder: BanCreateBuilder.() -> Unit) {
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
public suspend inline fun <reified T : GuildChannel> GuildBehavior.getChannelOf(channelId: Snowflake): T {
    val channel = supplier.getChannelOf<T>(channelId)
    require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
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
public suspend inline fun <reified T : GuildChannel> GuildBehavior.getChannelOfOrNull(channelId: Snowflake): T? {
    val channel = supplier.getChannelOfOrNull<T>(channelId) ?: return null
    require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
    return channel
}

public suspend inline fun GuildBehavior.editWidget(builder: GuildWidgetModifyBuilder.() -> Unit): GuildWidget {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
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
public inline fun GuildBehavior.getAuditLogEntries(builder: AuditLogGetRequestBuilder.() -> Unit = {}): Flow<AuditLogEntry> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return kord.with(rest).getAuditLogEntries(id, builder).map { AuditLogEntry(it, kord) }
}

/**
 * Executes a [RequestGuildMembers] command configured by the [builder] for guild
 * on this gateway, returning a flow of [MembersChunkEvent] responses.
 *
 * If no [builder] is specified, the request will be configured to fetch all members.
 *
 * The returned flow is cold, and will execute the request only on subscription.
 * Collection of this flow on a [Gateway] that is not [running][Gateway.start]
 * will result in an [IllegalStateException] being thrown.
 *
 * Executing the request on a [Gateway] with a [Shard][dev.kord.common.entity.DiscordShard] that
 * [does not match the guild id](https://discord.com/developers/docs/topics/gateway#sharding)
 * can result in undefined behavior for the returned flow and inconsistencies in the cache.
 *
 * This function expects [request.nonce][RequestGuildMembers.nonce] to contain a value, but it is not required.
 * If no nonce was provided one will be generated instead.
 */
@PrivilegedIntent
public inline fun GuildBehavior.requestMembers(builder: RequestGuildMembersBuilder.() -> Unit = { requestAllMembers() }): Flow<MembersChunkEvent> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = RequestGuildMembersBuilder(id).apply(builder).toRequest()
    return requestMembers(request)
}

/**
 * Creates a new [GuildScheduledEvent].
 */
public suspend fun GuildBehavior.createScheduledEvent(
    name: String,
    privacyLevel: GuildScheduledEventPrivacyLevel,
    scheduledStartTime: Instant,
    entityType: ScheduledEntityType,
    builder: ScheduledEventCreateBuilder.() -> Unit
): GuildScheduledEvent {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val event = kord.rest.guild.createScheduledEvent(id, name, privacyLevel, scheduledStartTime, entityType, builder)
    val data = GuildScheduledEventData.from(event)

    return GuildScheduledEvent(data, kord, supplier)
}


/**
 * Requests to create a new [KeywordAutoModerationRule] in this guild and returns it.
 *
 * This requires the [ManageGuild] permission.
 *
 * @param name the rule name.
 * @param eventType the rule [event type][AutoModerationRuleEventType].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.createKeywordAutoModerationRule(
    name: String,
    eventType: AutoModerationRuleEventType = MessageSend,
    builder: KeywordAutoModerationRuleCreateBuilder.() -> Unit,
): KeywordAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.createKeywordAutoModerationRule(guildId = id, name, eventType, builder)
    return KeywordAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}

/**
 * Requests to create a new [SpamAutoModerationRule] in this guild and returns it.
 *
 * This requires the [ManageGuild] permission.
 *
 * @param name the rule name.
 * @param eventType the rule [event type][AutoModerationRuleEventType].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.createSpamAutoModerationRule(
    name: String,
    eventType: AutoModerationRuleEventType = MessageSend,
    builder: SpamAutoModerationRuleCreateBuilder.() -> Unit,
): SpamAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.createSpamAutoModerationRule(guildId = id, name, eventType, builder)
    return SpamAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}

/**
 * Requests to create a new [KeywordPresetAutoModerationRule] in this guild and returns it.
 *
 * This requires the [ManageGuild] permission.
 *
 * @param name the rule name.
 * @param eventType the rule [event type][AutoModerationRuleEventType].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.createKeywordPresetAutoModerationRule(
    name: String,
    eventType: AutoModerationRuleEventType = MessageSend,
    builder: KeywordPresetAutoModerationRuleCreateBuilder.() -> Unit,
): KeywordPresetAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration.createKeywordPresetAutoModerationRule(guildId = id, name, eventType, builder)
    return KeywordPresetAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}

/**
 * Requests to create a new [MentionSpamAutoModerationRule] in this guild and returns it.
 *
 * This requires the [ManageGuild] permission.
 *
 * @param name the rule name.
 * @param eventType the rule [event type][AutoModerationRuleEventType].
 * @param mentionLimit total number of unique role and user mentions allowed per message (maximum of 50).
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun GuildBehavior.createMentionSpamAutoModerationRule(
    name: String,
    eventType: AutoModerationRuleEventType = MessageSend,
    mentionLimit: Int,
    builder: MentionSpamAutoModerationRuleCreateBuilder.() -> Unit,
): MentionSpamAutoModerationRule {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val rule = kord.rest.autoModeration
        .createMentionSpamAutoModerationRule(guildId = id, name, eventType, mentionLimit, builder)
    return MentionSpamAutoModerationRule(AutoModerationRuleData.from(rule), kord, supplier)
}
