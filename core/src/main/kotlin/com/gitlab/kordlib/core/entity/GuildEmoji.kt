package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.MemberBehavior
import com.gitlab.kordlib.core.behavior.RoleBehavior
import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.cache.data.EmojiData
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.toSnowflakeOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import java.util.*

/**
 * An instance of a [Discord emoji](https://discordapp.com/developers/docs/resources/emoji#emoji-object) belonging to a specific guild.
 */
class GuildEmoji(
        val data: EmojiData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Entity, Strategizable {

    override val id: Snowflake
        get() = Snowflake(data.id)

    val guildId: Snowflake
        get() = Snowflake(data.guildId)

    /**
     * Whether this emoji can be used, may be false due to loss of Server Boosts.
     */
    val isAvailable: Boolean get() = data.available

    /**
     * Whether is emoji is animated.
     */
    val isAnimated: Boolean get() = data.animated

    /**
     * Whether is emote is managed by Discord instead of the guild members.
     */
    val isManaged: Boolean get() = data.managed

    /**
     * The name of this emoji.
     *
     * This property can be null when trying to get the name of an emoji that was deleted.
     */
    val name: String? get() = data.name

    /**
     * Whether this emoji needs to be wrapped in colons.
     */
    val requiresColons: Boolean get() = data.requireColons

    /**
     * The ids of the [roles][Role] for which this emoji was whitelisted.
     */
    val roleIds: Set<Snowflake> get() = data.roles.asSequence().map { Snowflake(it) }.toSet()

    /**
     * The behaviors of the [roles][Role] for which this emoji was whitelisted.
     */
    val roleBehaviors: Set<RoleBehavior> get() = data.roles.asSequence().map { RoleBehavior(guildId = guildId, id = id, kord = kord) }.toSet()

    /**
     * The [roles][Role] for which this emoji was whitelisted.
     *
     * This request uses state [data] to resolve the entities belonging to the flow,
     * as such it can't guarantee an up to date representation if the [data] is outdated.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val roles: Flow<Role>
        get() = if (roleIds.isEmpty()) emptyFlow()
        else supplier.getGuildRoles(guildId).filter { it.id in roleIds }

    /**
     * The behavior of the [Member] who created the emote, if present.
     */
    val member: MemberBehavior? get() = userId?.let { MemberBehavior(guildId, it, kord) }

    /**
     * The id of the [User] who created the emote, if present.
     */
    val userId: Snowflake? get() = data.user?.id.toSnowflakeOrNull()

    /**
     * The [User] who created the emote, if present.
     */
    val user: UserBehavior? get() = userId?.let { UserBehavior(it, kord) }

    /**
     * Requests to get the creator of the emoji as a [Member],
     * returns null if the [Member] isn't present or [userId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getMember(): Member? = userId?.let { supplier.getMemberOrNull(guildId = guildId, userId = it) }

    /**
     * Requests to get the creator of the emoji as a [User],
     * returns null if the [User] isn't present or [userId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getUser(): User? = userId?.let { supplier.getUserOrNull(it) }

    /**
     * Returns a new [GuildEmoji] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>) = GuildEmoji(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when(other) {
        is GuildEmoji -> other.id == id && other.guildId == guildId
        else -> super.equals(other)
    }

}
