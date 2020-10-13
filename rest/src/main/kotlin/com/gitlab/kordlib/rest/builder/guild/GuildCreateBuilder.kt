package com.gitlab.kordlib.rest.builder.guild

import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.DefaultMessageNotificationLevel
import com.gitlab.kordlib.common.entity.ExplicitContentFilter
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.VerificationLevel
import com.gitlab.kordlib.rest.builder.RequestBuilder
import com.gitlab.kordlib.rest.builder.channel.CategoryCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.NewsChannelCreateBuilder
import com.gitlab.kordlib.rest.builder.channel.TextChannelCreateBuilder
import com.gitlab.kordlib.rest.builder.role.RoleCreateBuilder
import com.gitlab.kordlib.rest.json.request.GuildCreateChannelRequest
import com.gitlab.kordlib.rest.json.request.GuildCreateRequest
import com.gitlab.kordlib.rest.json.request.GuildRoleCreateRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.random.Random

@KordDsl
class GuildCreateBuilder : RequestBuilder<GuildCreateRequest> {

    /**
     * Iterator that generates unique ids for roles and channels..
     */
    val snowflakeGenerator by lazy(LazyThreadSafetyMode.NONE) {
        generateSequence { Random.nextLong(0, Long.MAX_VALUE) }.filter {
            it !in roles.map { role -> role.id?.toLong() }
                    && it !in channels.map { channel -> channel.id?.toLong() }
                    && Snowflake(it) != systemChannelId
                    && Snowflake(it) != afkChannelId
        }.iterator()
    }

    /**
     * Generates a new unique [Snowflake] using the [snowflakeGenerator].
     */
    fun newUniqueSnowflake() = Snowflake(snowflakeGenerator.next())

    lateinit var name: String
    var region: String? = null
    var icon: String? = null
    var verificationLevel: VerificationLevel? = null
    var defaultMessageNotificationLevel: DefaultMessageNotificationLevel? = null
    var explicitContentFilter: ExplicitContentFilter? = null
    var everyoneRole: RoleCreateBuilder? = null
    val roles: MutableList<GuildRoleCreateRequest> = mutableListOf()
    val channels: MutableList<GuildCreateChannelRequest> = mutableListOf()

    /**
     * The id of the afk channel, this channel can be configured by supplying a channel with the same id.
     */
    val afkChannelId: Snowflake? = null

    /**
     * The afk timeout in seconds.
     */
    val afkTimeout: Int? = null

    /**
     * The id of the channel to which system messages are sent, this channel can be configured by supplying a channel with the same id.
     */
    val systemChannelId: Snowflake? = null

    @OptIn(ExperimentalContracts::class)
    inline fun textChannel(id: Snowflake = newUniqueSnowflake(), builder: TextChannelCreateBuilder.() -> Unit): Snowflake {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        channels.add(TextChannelCreateBuilder().apply(builder).toRequest().copy(id = id.value))
        return id
    }

    @OptIn(ExperimentalContracts::class)
    inline fun newsChannel(id: Snowflake = newUniqueSnowflake(), builder: NewsChannelCreateBuilder.() -> Unit): Snowflake {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        channels.add(NewsChannelCreateBuilder().apply(builder).toRequest().copy(id = id.value))
        return id
    }

    @OptIn(ExperimentalContracts::class)
    inline fun category(id: Snowflake = newUniqueSnowflake(), builder: CategoryCreateBuilder.() -> Unit): Snowflake {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        channels.add(CategoryCreateBuilder().apply(builder).toRequest().copy(id = id.value))
        return id
    }

    @OptIn(ExperimentalContracts::class)
    inline fun role(id: Snowflake = newUniqueSnowflake(), builder: RoleCreateBuilder.() -> Unit): Snowflake {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        roles += RoleCreateBuilder().apply(builder).toRequest().copy(id = id.value)
        return id
    }

    @OptIn(ExperimentalContracts::class)
    inline fun everyoneRole(builder: RoleCreateBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        everyoneRole = RoleCreateBuilder().apply(builder)
    }

    override fun toRequest(): GuildCreateRequest = GuildCreateRequest(
            name,
            region,
            icon,
            verificationLevel,
            defaultMessageNotificationLevel,
            explicitContentFilter,
            if (roles.isEmpty()) null else everyoneRole?.let { roles + it.toRequest() } ?: roles,
            if (channels.isEmpty()) null else channels
    )
}
