package dev.kord.core.entity

import dev.kord.common.Color
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.core.Kord
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.cache.data.RoleData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import java.util.*

/**
 * An instance of a [Discord role](https://discord.com/developers/docs/topics/permissions#role-object)
 *
 * @param data The [RoleData] for the role
 */
public data class Role(
    val data: RoleData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : RoleBehavior {

    override val id: Snowflake
        get() = data.id

    override val guildId: Snowflake
        get() = data.guildId

    /** The colour of the role, as a [Color] object. */
    public val color: Color get() = Color(data.color)

    /** If this role is pinned in the user listing. */
    public val hoisted: Boolean get() = data.hoisted

    /** The icon of the role as an [Icon] object. */
    val icon: Icon? get() = data.icon.value?.let { Icon.RoleIcon(data.id, it, kord) }

    /** The unicode of the role emoji. */
    val unicodeEmoji: String? = data.unicodeEmoji.value

    /** Whether an integration manages this role or not. */
    public val managed: Boolean get() = data.managed

    /** Whether this role is mentionable or not. */
    public val mentionable: Boolean get() = data.mentionable

    /** The name of this role. */
    public val name: String get() = data.name

    /** The [Permissions] set for this role. */
    public val permissions: Permissions get() = data.permissions

    /** The position of this role in the list. */
    public val rawPosition: Int get() = data.position

    override suspend fun asRole(): Role = this

    override suspend fun asRoleOrNull(): Role = this

    /**
     * The tags of this role, if present.
     */
    public val tags: RoleTags? get() = data.tags.unwrap { RoleTags(it, guildId, kord) }

    override fun compareTo(other: Entity): Int = when (other) {
        is Role -> compareBy<Role> { it.rawPosition }.thenBy { it.guildId }.compare(this, other)
        else -> super.compareTo(other)
    }

    /**
     * Returns a new [Role] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Role = Role(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is RoleBehavior -> other.id == id && other.guildId == guildId
        else -> false
    }

    override fun toString(): String {
        return "Role(data=$data, kord=$kord, supplier=$supplier)"
    }

}
