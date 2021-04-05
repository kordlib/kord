package dev.kord.core

import dev.kord.cache.api.DataCache
import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.DiscordShard
import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.builder.kord.KordBuilder
import dev.kord.core.builder.kord.KordRestOnlyBuilder
import dev.kord.core.cache.data.GuildData
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.*
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.interaction.GlobalApplicationCommand
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.exception.KordInitializationException
import dev.kord.core.gateway.MasterGateway
import dev.kord.core.gateway.handler.GatewayEventInterceptor
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.gateway.Gateway
import dev.kord.gateway.builder.PresenceBuilder
import dev.kord.rest.builder.guild.GuildCreateBuilder
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder
import dev.kord.rest.builder.interaction.ApplicationCommandsCreateBuilder
import dev.kord.rest.builder.user.CurrentUserModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.channels.Channel as CoroutineChannel

val kordLogger = KotlinLogging.logger { }


/**
 * The central adapter between other Kord modules and source of core [events].
 */
class Kord(
    val resources: ClientResources,
    val cache: DataCache,
    val gateway: MasterGateway,
    val rest: RestClient,
    val selfId: Snowflake,
    private val eventFlow: MutableSharedFlow<Event>,
    dispatcher: CoroutineDispatcher,
) : CoroutineScope {
    private val interceptor = GatewayEventInterceptor(this, gateway, cache, eventFlow)

    /**
     * A [SlashCommands] object to deal with Application Commands interactions.
     */
    @KordPreview
    val slashCommands: SlashCommands = SlashCommands(selfId, rest.interaction)

    /**
     * Global commands made by the bot under this Kord instance.
     */
    @KordPreview
    val globalCommands: Flow<GlobalApplicationCommand>
        get() = slashCommands.getGlobalApplicationCommands()

    /**
     * The default supplier, obtained through Kord's [resources] and configured through [KordBuilder.defaultStrategy].
     * By default a strategy from [EntitySupplyStrategy.rest].
     *
     * All [strategizable][Strategizable] [entities][KordEntity] created through this instance will use this supplier by default.
     */
    val defaultSupplier: EntitySupplier = resources.defaultStrategy.supply(this)

    /**
     * A reference to all exposed [unsafe][KordUnsafe] entity constructors for this instance.
     */
    @OptIn(KordUnsafe::class, KordExperimental::class)
    val unsafe: Unsafe = Unsafe(this)

    /**
     * The events emitted from the [gateway]. Call [Kord.login] to start receiving events.
     *
     * Events emitted by this flow are guaranteed to follow the same order as they were received
     * by the web socket. Any event flow order between multiple [MasterGateway.gateways] is not
     * guaranteed.
     *
     * Subscriptions to this flow will not complete normally, as per design of [SharedFlow].
     * Use [Flow.launchIn] with [Kord] as [CoroutineScope] to cease event processing
     * on [Kord.shutdown].
     *
     * Behavior like replay cache size, buffer size and overflow behavior are dependant on the
     * supplied [eventFlow]. See [KordBuilder.eventFlow] for more details.
     */
    val events: SharedFlow<Event>
        get() = eventFlow

    override val coroutineContext: CoroutineContext = dispatcher + SupervisorJob()

    val regions: Flow<Region>
        get() = defaultSupplier.regions

    val guilds: Flow<Guild>
        get() = defaultSupplier.guilds

    init {
        launch { interceptor.start() }
    }

    /**
     * Logs in to the configured [Gateways][Gateway]. Suspends until [logout] or [shutdown] is called.
     */
    @OptIn(ExperimentalContracts::class)
    suspend inline fun login(builder: PresenceBuilder.() -> Unit = { status = PresenceStatus.Online }) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        gateway.start(resources.token) {
            shard = DiscordShard(0, resources.shards.totalShards)
            presence(builder)
            intents = resources.intents
            name = "kord"
        }
    }

    /**
     * Logs out to the configured [Gateways][Gateway].
     */
    suspend fun logout() = gateway.stopAll()

    /**
     * Logs out of all connected [Gateways][Gateway] and frees all resources.
     */
    suspend fun shutdown() {
        gateway.detachAll()

        // resolve ambiguous coroutineContext
        (this as CoroutineScope).cancel()
    }

    fun <T : EntitySupplier> with(strategy: EntitySupplyStrategy<T>): T = strategy.supply(this)

    suspend fun getApplicationInfo(): ApplicationInfo = with(EntitySupplyStrategy.rest).getApplicationInfo()

    /**
     * Requests to create a new Guild configured through the [builder].
     * At least the [GuildCreateBuilder.name] has to be set.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @return The newly created Guild.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated(
        "guild name is a mandatory field",
        ReplaceWith("createGuild(\"name\", builder)"),
        DeprecationLevel.WARNING
    )
    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuild(builder: GuildCreateBuilder.() -> Unit): Guild {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        return createGuild("name", builder)
    }

    /**
     * Requests to create a new Guild configured through the [builder].
     * At least the [GuildCreateBuilder.name] has to be set.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @return The newly created Guild.
     */
    @OptIn(ExperimentalContracts::class)
    suspend inline fun createGuild(name: String, builder: GuildCreateBuilder.() -> Unit): Guild {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val response = rest.guild.createGuild(name, builder)
        val data = GuildData.from(response)

        return Guild(data, this)
    }

    /**
     * Requests to get the [GuildPreview] of a guild with the [guildId] through the [strategy],
     * returns null if the [GuildPreview] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the preview wasn't present.
     */
    suspend fun getGuildPreview(
        guildId: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy,
    ): GuildPreview = strategy.supply(this).getGuildPreview(guildId)

    /**
     * Requests to get the [GuildPreview] of a guild with the [guildId] through the [strategy],
     * returns null if the [GuildPreview] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildPreviewOrNull(
        guildId: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy,
    ): GuildPreview? = strategy.supply(this).getGuildPreviewOrNull(guildId)


    /**
     * Requests to get the [Channel] with the [id] through the [strategy],
     * returns null if the [Channel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     */
    suspend fun getChannel(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> =
            resources.defaultStrategy,
    ): Channel? = strategy.supply(this).getChannelOrNull(id)

    /**
     * Requests to get the [Channel] as type [T] through the [strategy],
     * returns null if the [Channel] isn't present or is not of type [T].
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend inline fun <reified T : Channel> getChannelOf(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy,
    ): T? = strategy.supply(this).getChannelOfOrNull(id)

    suspend fun getGuild(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> =
            resources.defaultStrategy,
    ): Guild? = strategy.supply(this).getGuildOrNull(id)

    /**
     * Requests to get the [Webhook] in this guild.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the webhook was not present.
     */
    suspend fun getWebhook(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook = strategy.supply(this).getWebhook(id)

    /**
     * Requests to get the [Webhook] in this guild with an authentication token,
     * returns null if the webhook was not present.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */

    suspend fun getWebhookOrNull(
        id: Snowflake,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook? = strategy.supply(this).getWebhookOrNull(id)

    /**
     * Requests to get the [Webhook] in this guild with an authentication token.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the webhook was not present.
     */
    suspend fun getWebhookWithToken(
        id: Snowflake,
        token: String,
        strategy: EntitySupplyStrategy<*> = resources.defaultStrategy
    ): Webhook = strategy.supply(this).getWebhookWithToken(id, token)

    /**
     * Requests to get the [Webhook] in this guild with an authentication token,
     * returns null if the webhook was not present.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */

    suspend fun getWebhookWithTokenOrNull(id: Snowflake, token: String, strategy: EntitySupplyStrategy<*>): Webhook? =
        strategy.supply(this).getWebhookWithTokenOrNull(id, token)


    /**
     * Requests to get the [User] that represents this bot account through the [strategy],
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    suspend fun getSelf(strategy: EntitySupplyStrategy<*> = resources.defaultStrategy): User =
        strategy.supply(this).getSelf()

    @OptIn(ExperimentalContracts::class)
    suspend fun editSelf(builder: CurrentUserModifyBuilder.() -> Unit): User {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return User(UserData.from(rest.user.modifyCurrentUser(builder)), this)
    }

    /**
     * Requests to get the [User] that with the [id] through the [strategy],
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    suspend fun getUser(id: Snowflake, strategy: EntitySupplyStrategy<*> = resources.defaultStrategy): User? =
        strategy.supply(this).getUserOrNull(id)

    /**
     * Requests to get the [Invite] with [code] through the [EntitySupplyStrategy.rest][rest].
     * The returned [Invite], if found, uses the default strategy used by Kord.
     *
     * @throws [RequestException] if anything went wrong during the request.
     *
     */
    suspend fun getInvite(
        code: String,
        withCounts: Boolean,
    ): Invite? =
        EntitySupplyStrategy.rest.supply(this).getInviteOrNull(code, withCounts)


    /**
     * Requests to edit the presence of the bot user configured by the [builder].
     * The new presence will be shown on all shards. Use [MasterGateway.gateways] or [Event.gateway] to
     * set the presence of a single shard.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    @OptIn(ExperimentalContracts::class)
    suspend inline fun editPresence(builder: PresenceBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val status = PresenceBuilder().apply(builder).toUpdateStatus()
        gateway.sendAll(status)
    }

    override fun equals(other: Any?): Boolean {
        val kord = other as? Kord ?: return false

        return resources.token == kord.resources.token
    }

    override fun toString(): String {
        return "Kord(resources=$resources, cache=$cache, gateway=$gateway, rest=$rest, selfId=$selfId)"
    }

    companion object {

        /**
         * Builds a [Kord] instance configured by the [builder].
         *
         * The instance only allows for configuration of REST related APIs,
         * interacting with the [gateway][Kord.gateway] or its [events][Kord.events] will result in no-ops.
         *
         * Similarly, [cache][Kord.cache] related functionality has been disabled and
         * replaced with a no-op implementation.
         */
        @OptIn(ExperimentalContracts::class)
        @KordExperimental
        inline fun restOnly(token: String, builder: KordRestOnlyBuilder.() -> Unit = {}): Kord {
            contract {
                callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
            }
            return KordRestOnlyBuilder(token).apply(builder).build()
        }
    }


    @OptIn(ExperimentalContracts::class)
    @KordPreview
    suspend inline fun createGlobalApplicationCommand(
        name: String,
        description: String,
        builder: ApplicationCommandCreateBuilder.() -> Unit = {},
    ): GlobalApplicationCommand {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return slashCommands.createGlobalApplicationCommand(name, description, builder)
    }

    @OptIn(ExperimentalContracts::class)
    @KordPreview
    suspend inline fun createGlobalApplicationCommands(
        builder: ApplicationCommandsCreateBuilder.() -> Unit,
    ): Flow<GlobalApplicationCommand> {

        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return slashCommands.createGlobalApplicationCommands(builder)

    }


}

/**
 * Builds a [Kord] instance configured by the [builder].
 *
 * @throws KordInitializationException if something went wrong while getting the bot's gateway information.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun Kord(token: String, builder: KordBuilder.() -> Unit = {}): Kord {
contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return KordBuilder(token).apply(builder).build()
}


/**
 * Convenience method that will invoke the [consumer] on every event [T] created by [Kord.events].
 *
 * The events are buffered in an [unlimited][CoroutineChannel.UNLIMITED] [buffer][Flow.buffer] and
 * [launched][CoroutineScope.launch] in the supplied [scope], which is [Kord] by default.
 * Each event will be [launched][CoroutineScope.launch] inside the [scope] separately and
 * any thrown [Throwable] will be caught and logged.
 *
 * The returned [Job] is a reference to the created coroutine, call [Job.cancel] to cancel the processing of any further
 * events for this [consumer].
 */
inline fun <reified T : Event> Kord.on(scope: CoroutineScope = this, noinline consumer: suspend T.() -> Unit): Job =
    events.buffer(CoroutineChannel.UNLIMITED).filterIsInstance<T>()
        .onEach {
            scope.launch { runCatching { consumer(it) }.onFailure { kordLogger.catching(it) } }
        }
        .launchIn(scope)
