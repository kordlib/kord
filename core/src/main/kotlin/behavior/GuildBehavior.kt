package dev.kord.core.behavior

import dev.kord.cache.api.query
import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.cache.data.*
import dev.kord.core.cache.idEq
import dev.kord.core.catchDiscordError
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.*
import dev.kord.core.entity.interaction.GuildApplicationCommand
import dev.kord.core.event.guild.MembersChunkEvent
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.sorted
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.EntitySupplyStrategy.Companion.rest
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.gateway.Gateway
import dev.kord.gateway.PrivilegedIntent
import dev.kord.gateway.RequestGuildMembers
import dev.kord.gateway.builder.RequestGuildMembersBuilder
import dev.kord.gateway.start
import dev.kord.rest.Image
import dev.kord.rest.builder.auditlog.AuditLogGetRequestBuilder
import dev.kord.rest.builder.ban.BanCreateBuilder
import dev.kord.rest.builder.channel.*
import dev.kord.rest.builder.guild.EmojiCreateBuilder
import dev.kord.rest.builder.guild.GuildModifyBuilder
import dev.kord.rest.builder.guild.GuildWidgetModifyBuilder
import dev.kord.rest.builder.guild.WelcomeScreenModifyBuilder
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder
import dev.kord.rest.builder.interaction.ApplicationCommandsCreateBuilder
import dev.kord.rest.builder.role.RoleCreateBuilder
import dev.kord.rest.builder.role.RolePositionsModifyBuilder
import dev.kord.rest.json.JsonErrorCode
import dev.kord.rest.json.request.CurrentUserNicknameModifyRequest
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Guild](https://discord.com/developers/docs/resources/guild).
 */
interface GuildBehavior : KordEntity, Strategizable {
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
     * This property is not resolvable through REST and will always use [Kord.cache] instead.
     */
    val presences: Flow<Presence>
        get() = kord.cache.query<PresenceData> { idEq(PresenceData::guildId, id) }
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
     * This property is not resolvable through REST and will always use [Kord.cache] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val voiceStates: Flow<VoiceState>
        get() = kord.cache
            .query<VoiceStateData> { idEq(VoiceStateData::guildId, id) }
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

    val templates: Flow<Template>
        get() = supplier.getTemplates(id)

    /**
     * Application commands for this guild only.
     */
    @KordPreview
    val commands: Flow<GuildApplicationCommand>
        get() = kord.slashCommands.getGuildApplicationCommands(id)

    /**
     * Returns the gateway this guild is part of as per the
     * [Discord sharding rules](https://discord.com/developers/docs/topics/gateway#sharding).
     * Returns `null` if no gateway for the shard is present in [kord].
     */
    val gateway: Gateway?
        get() {
            val shard = id.value.shr(22) % kord.resources.shards.totalShards.coerceAtLeast(1)
            return kord.gateway.gateways[shard.toInt()]
        }

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
    fun requestMembers(request: RequestGuildMembers): Flow<MembersChunkEvent> {
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
     * Requests to get up to [limit] members whose [Member.username] or [Member.nickname] match the [query].
     * The [limit] accepts a maximum value of `1000` and a minimum of `1`.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     *
     * This function is not part of the officially documented Discord API and may be removed/altered/stop working in the future.
     */
    @KordExperimental
    suspend fun getMembers(query: String, limit: Int = 1000): Flow<Member> = flow {
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
    suspend fun modifySelfNickname(newNickname: String? = null): String = editSelfNickname(newNickname)

    /**
     *  Requests to change the nickname of the bot in this guild, passing `null` will remove it.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun editSelfNickname(newNickname: String? = null): String {
        return kord.rest.guild.modifyCurrentUserNickname(
            id,
            CurrentUserNicknameModifyRequest(Optional(newNickname))
        ).nick
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
        require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
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
        require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
        return channel
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

    suspend fun getWelcomeScreenOrNull(): WelcomeScreen? =
        rest.supply(kord).getGuildWelcomeScreenOrNull(id)

    suspend fun getWelcomeScreen(): WelcomeScreen =
        rest.supply(kord).getGuildWelcomeScreen(id)

    suspend fun editWelcomeScreen(builder: WelcomeScreenModifyBuilder.() -> Unit): WelcomeScreen {
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
    suspend fun getVanityUrl(): String? {
        val identifier = catchDiscordError(JsonErrorCode.InviteCodeInvalidOrTaken) {
            kord.rest.guild.getVanityInvite(id).code
        } ?: return null
        return "https://discord.gg/$identifier"
    }

    suspend fun getWidget(): GuildWidget = supplier.getGuildWidget(id)

    suspend fun getWidgetOrNull(): GuildWidget? = supplier.getGuildWidgetOrNull(id)

    suspend fun getTemplate(code: String) = supplier.getTemplate(code)

    suspend fun getTemplateOrNull(code: String) = supplier.getTemplateOrNull(code)

    /**
     * Returns a new [GuildBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildBehavior = GuildBehavior(id, kord, strategy)
}

 fun GuildBehavior(
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy,
) = object : GuildBehavior {
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


@OptIn(ExperimentalContracts::class)
@KordPreview
suspend inline fun GuildBehavior.createApplicationCommand(
    name: String,
    description: String,
    builder: ApplicationCommandCreateBuilder.() -> Unit = {},
): GuildApplicationCommand {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return kord.slashCommands.createGuildApplicationCommand(id, name, description, builder)
}


@OptIn(ExperimentalContracts::class)
@KordPreview
suspend inline fun GuildBehavior.createApplicationCommands(
    builder: ApplicationCommandsCreateBuilder.() -> Unit
): Flow<GuildApplicationCommand> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return kord.slashCommands.createGuildApplicationCommands(id, builder)
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

@Deprecated(
    "emoji name and image are mandatory fields.",
    ReplaceWith("createEmoji(\"name\", Image.fromUrl(\"url\"), builder)")
)
@DeprecatedSinceKord("0.7.0")
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createEmoji(builder: EmojiCreateBuilder.() -> Unit): GuildEmoji {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createEmoji("name", Image.raw(byteArrayOf(), Image.Format.PNG), builder)
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createEmoji(
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
@Deprecated(
    "channel name is a mandatory field",
    ReplaceWith("createTextChannel(\"name\", builder)"),
    DeprecationLevel.WARNING
)
@DeprecatedSinceKord("0.7.0")
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createTextChannel(builder: TextChannelCreateBuilder.() -> Unit): TextChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createTextChannel("name", builder)
}

/**
 * Requests to create a new text channel.
 *
 * @return The created [TextChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createTextChannel(
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

/**
 * Requests to create a new voice channel.
 *
 * @return The created [VoiceChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@Deprecated(
    "channel name is a mandatory field.",
    ReplaceWith("createVoiceChannel(\"name\", builder)"),
    DeprecationLevel.WARNING
)
@DeprecatedSinceKord("0.7.0")
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createVoiceChannel(builder: VoiceChannelCreateBuilder.() -> Unit): VoiceChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createVoiceChannel("name", builder)
}

/**
 * Requests to create a new voice channel.
 *
 * @return The created [VoiceChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createVoiceChannel(
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
@Deprecated(
    "channel name is a mandatory field.",
    ReplaceWith("createNewsChannel(\"name\", builder)"),
    DeprecationLevel.WARNING
)
@DeprecatedSinceKord("0.7.0")
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createNewsChannel(builder: NewsChannelCreateBuilder.() -> Unit): NewsChannel {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createNewsChannel("name", builder)
}

/**
 * Requests to create a new news channel.
 *
 * @return The created [NewsChannel].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createNewsChannel(
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
@Deprecated(
    "channel name is a mandatory field.",
    ReplaceWith("createCategoryChannel(\"name\", builder)"),
    DeprecationLevel.WARNING
)
@DeprecatedSinceKord("0.7.0")
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createCategory(builder: CategoryCreateBuilder.() -> Unit): Category {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    return createCategory("name", builder)
}

/**
 * Requests to create a new category.
 *
 * @return The created [Category].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.createCategory(
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
suspend inline fun <reified T : GuildChannel> GuildBehavior.getChannelOfOrNull(channelId: Snowflake): T? {
    val channel = supplier.getChannelOfOrNull<T>(channelId) ?: return null
    require(channel.guildId == this.id) { "channel ${channelId.value} is not in guild ${this.id}" }
    return channel
}

@OptIn(ExperimentalContracts::class)
suspend inline fun GuildBehavior.editWidget(builder: GuildWidgetModifyBuilder.() -> Unit): GuildWidget {
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
@OptIn(ExperimentalContracts::class)
inline fun GuildBehavior.getAuditLogEntries(builder: AuditLogGetRequestBuilder.() -> Unit = {}): Flow<AuditLogEntry> {
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
@OptIn(ExperimentalContracts::class)
@PrivilegedIntent
inline fun GuildBehavior.requestMembers(builder: RequestGuildMembersBuilder.() -> Unit = { requestAllMembers() }): Flow<MembersChunkEvent> {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = RequestGuildMembersBuilder(id).apply(builder).toRequest()
    return requestMembers(request)
}
