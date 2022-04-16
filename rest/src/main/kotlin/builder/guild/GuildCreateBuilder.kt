package dev.kord.rest.builder.guild

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DefaultMessageNotificationLevel
import dev.kord.common.entity.ExplicitContentFilter
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.VerificationLevel
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.channel.CategoryCreateBuilder
import dev.kord.rest.builder.channel.NewsChannelCreateBuilder
import dev.kord.rest.builder.channel.TextChannelCreateBuilder
import dev.kord.rest.builder.role.RoleCreateBuilder
import dev.kord.rest.json.request.GuildChannelCreateRequest
import dev.kord.rest.json.request.GuildCreateRequest
import dev.kord.rest.json.request.GuildRoleCreateRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.time.Duration

@KordDsl
public class GuildCreateBuilder(public var name: String) : RequestBuilder<GuildCreateRequest> {

    /**
     * Iterator that generates unique ids for roles and channels.
     */
    public val snowflakeGenerator: Iterator<Snowflake> by lazy(LazyThreadSafetyMode.NONE) {
        generateSequence { Random.nextULong(Snowflake.validValues) }
            .map { Snowflake(it) }
            .filter {
                it !in roles.map { role -> role.id.value }
                        && it !in channels.map { channel -> channel.id.value }
                        && it != systemChannelId
                        && it != afkChannelId
            }
            .iterator()
    }

    /**
     * Generates a new unique [Snowflake] using the [snowflakeGenerator].
     */
    public fun newUniqueSnowflake(): Snowflake = snowflakeGenerator.next()

    private var _region: Optional<String> = Optional.Missing()
    public var region: String? by ::_region.delegate()

    private var _icon: Optional<Image> = Optional.Missing()
    public var icon: Image? by ::_icon.delegate()

    private var _verificationLevel: Optional<VerificationLevel> = Optional.Missing()
    public var verificationLevel: VerificationLevel? by ::_verificationLevel.delegate()

    private var _defaultMessageNotificationLevel: Optional<DefaultMessageNotificationLevel> = Optional.Missing()
    public var defaultMessageNotificationLevel: DefaultMessageNotificationLevel? by ::_defaultMessageNotificationLevel.delegate()

    private var _explicitContentFilter: Optional<ExplicitContentFilter> = Optional.Missing()
    public var explicitContentFilter: ExplicitContentFilter? by ::_explicitContentFilter.delegate()

    private var _everyoneRole: Optional<RoleCreateBuilder> = Optional.Missing()
    public var everyoneRole: RoleCreateBuilder? by ::_everyoneRole.delegate()

    public val roles: MutableList<GuildRoleCreateRequest> = mutableListOf()
    public val channels: MutableList<GuildChannelCreateRequest> = mutableListOf()

    private var _afkChannelId: OptionalSnowflake = OptionalSnowflake.Missing

    /**
     * The id of the afk channel, this channel can be configured by supplying a channel with the same id.
     */
    public var afkChannelId: Snowflake? by ::_afkChannelId.delegate()

    private var _afkTimeout: Optional<Duration> = Optional.Missing()

    /**
     * The afk timeout.
     */
    public var afkTimeout: Duration? by ::_afkTimeout.delegate()

    private var _systemChannelId: OptionalSnowflake = OptionalSnowflake.Missing

    /**
     * The id of the channel to which system messages are sent, this channel can be configured by supplying a channel with the same id.
     */
    public var systemChannelId: Snowflake? by ::_systemChannelId.delegate()

    public inline fun textChannel(
        name: String,
        id: Snowflake = newUniqueSnowflake(),
        builder: TextChannelCreateBuilder.() -> Unit
    ): Snowflake {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        channels.add(TextChannelCreateBuilder(name).apply(builder).toRequest().copy(id = OptionalSnowflake.Value(id)))
        return id
    }

    public inline fun newsChannel(
        name: String,
        id: Snowflake = newUniqueSnowflake(),
        builder: NewsChannelCreateBuilder.() -> Unit
    ): Snowflake {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        channels.add(NewsChannelCreateBuilder(name).apply(builder).toRequest().copy(id = OptionalSnowflake.Value(id)))
        return id
    }

    public inline fun category(
        name: String,
        id: Snowflake = newUniqueSnowflake(),
        builder: CategoryCreateBuilder.() -> Unit
    ): Snowflake {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        channels.add(CategoryCreateBuilder(name).apply(builder).toRequest().copy(id = OptionalSnowflake.Value(id)))
        return id
    }

    public inline fun role(id: Snowflake = newUniqueSnowflake(), builder: RoleCreateBuilder.() -> Unit): Snowflake {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        roles += RoleCreateBuilder().apply(builder).toRequest().copy(id = OptionalSnowflake.Value(id))
        return id
    }

    public inline fun everyoneRole(builder: RoleCreateBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        everyoneRole = RoleCreateBuilder().apply(builder)
    }

    override fun toRequest(): GuildCreateRequest = GuildCreateRequest(
        name,
        _region,
        _icon.map { it.dataUri },
        _verificationLevel,
        _defaultMessageNotificationLevel,
        _explicitContentFilter,
        Optional.missingOnEmpty(roles).map { roles ->
            when (val everyone = everyoneRole?.toRequest()) {
                null -> roles
                else -> mutableListOf(everyone).also { it.addAll(roles) }
            }
        },
        Optional.missingOnEmpty(channels),
        _afkChannelId,
        _afkTimeout,
        _systemChannelId,
    )
}
