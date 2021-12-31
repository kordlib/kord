package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.EmojiData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.guild.EmojiModifyBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * An instance of a [Discord emoji](https://discord.com/developers/docs/resources/emoji#emoji-object) belonging to a specific guild.
 */
public class GuildEmoji(
    public val data: EmojiData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity, Strategizable {

    override val id: Snowflake
        get() = data.id

    public val guildId: Snowflake
        get() = data.guildId

    public val mention: String
        get() = if (isAnimated) "<a:$name:$id>" else "<:$name:$id>"

    /**
     * Whether this emoji can be used, may be false due to loss of Server Boosts.
     */
    public val isAvailable: Boolean get() = data.available.discordBoolean

    /**
     * Whether is emoji is animated.
     */
    public val isAnimated: Boolean get() = data.animated.discordBoolean

    /**
     * Whether is emote is managed by Discord instead of the guild members.
     */
    public val isManaged: Boolean get() = data.managed.discordBoolean

    /**
     * The name of this emoji.
     *
     * This property can be null when trying to get the name of an emoji that was deleted.
     */
    public val name: String? get() = data.name

    /**
     * Whether this emoji needs to be wrapped in colons.
     */
    public val requiresColons: Boolean get() = data.requireColons.discordBoolean

    /**
     * The ids of the [roles][Role] for which this emoji was whitelisted.
     */
    public val roleIds: Set<Snowflake> get() = data.roles.orEmpty().toSet()

    /**
     * The behaviors of the [roles][Role] for which this emoji was whitelisted.
     */
    public val roleBehaviors: Set<RoleBehavior>
        get() = data.roles.orEmpty().map { RoleBehavior(guildId = guildId, id = id, kord = kord) }.toSet()

    /**
     * The [roles][Role] for which this emoji was whitelisted.
     *
     * This request uses state [data] to resolve the entities belonging to the flow,
     * as such it can't guarantee an up to date representation if the [data] is outdated.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val roles: Flow<Role>
        get() = if (roleIds.isEmpty()) emptyFlow()
        else supplier.getGuildRoles(guildId).filter { it.id in roleIds }

    /**
     * The behavior of the [Member] who created the emote, if present.
     */
    public val member: MemberBehavior? get() = userId?.let { MemberBehavior(guildId, it, kord) }

    /**
     * The id of the [User] who created the emote, if present.
     */
    public val userId: Snowflake? get() = data.userId.value

    /**
     * The [User] who created the emote, if present.
     */
    public val user: UserBehavior? get() = userId?.let { UserBehavior(it, kord) }

    /**
     * The image as [Icon] object for the emoji
     */
    public val image: Icon get() = Icon.EmojiIcon(data.animated.discordBoolean, data.id, kord)

    /**
     * Requests to delete this emoji, with the given [reason].
     *
     * @param reason the reason showing up in the audit log
     * @throws RequestException if anything went wrong during the request.
     */
    public suspend fun delete(reason: String? = null) {
        kord.rest.emoji.deleteEmoji(guildId = guildId, emojiId = id, reason = reason)
    }

    /**
     *  Requests to edit the emoji.
     *
     *  @throws [RequestException] if anything went wrong during the request.
     */
    public suspend inline fun edit(builder: EmojiModifyBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        kord.rest.emoji.modifyEmoji(guildId = guildId, emojiId = id, builder = builder)
    }

    /**
     * Requests to get the creator of the emoji as a [Member],
     * returns null if the [Member] isn't present or [userId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getMember(): Member? = userId?.let { supplier.getMemberOrNull(guildId = guildId, userId = it) }

    /**
     * Requests to get the creator of the emoji as a [User],
     * returns null if the [User] isn't present or [userId] is null.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getUser(): User? = userId?.let { supplier.getUserOrNull(it) }

    /**
     * Returns a new [GuildEmoji] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildEmoji = GuildEmoji(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildEmoji -> other.id == id && other.guildId == guildId
        else -> super.equals(other)
    }

    override fun toString(): String {
        return "GuildEmoji(data=$data, kord=$kord, supplier=$supplier)"
    }

}
