package dev.kord.rest.builder.channel

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Snowflake
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Builder that can modify [permission overwrites][Overwrite].
 */
public sealed interface PermissionOverwritesBuilder {
    /**
     * Adds [overwrite] to this builder.
     */
    public fun addOverwrite(overwrite: Overwrite)
}

/**
 * [PermissionOverwritesBuilder] which creates an entity with overwrites.
 */
public interface PermissionOverwritesCreateBuilder : PermissionOverwritesBuilder {
    public var permissionOverwrites: MutableSet<Overwrite>

    override fun addOverwrite(overwrite: Overwrite) {
        permissionOverwrites.add(overwrite)
    }
}

/**
 * [PermissionOverwritesBuilder] which modifies an existing entity with overwrites.
 */
public interface PermissionOverwritesModifyBuilder : PermissionOverwritesBuilder {
    public var permissionOverwrites: MutableSet<Overwrite>?

    override fun addOverwrite(overwrite: Overwrite) {
        val overwrites = permissionOverwrites ?: mutableSetOf()
        overwrites.add(overwrite)

        permissionOverwrites = overwrites
    }
}

/**
 * Adds an [Overwrite] for the [memberId].
 */
public inline fun PermissionOverwritesBuilder.addMemberOverwrite(
    memberId: Snowflake,
    builder: PermissionOverwriteBuilder.() -> Unit
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val overwrite = PermissionOverwriteBuilder(OverwriteType.Member, memberId).apply(builder).toOverwrite()

    return addOverwrite(overwrite)
}

/**
 * Adds an [Overwrite] for the [roleId].
 */
public inline fun PermissionOverwritesBuilder.addRoleOverwrite(
    roleId: Snowflake,
    builder: PermissionOverwriteBuilder.() -> Unit
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val overwrite = PermissionOverwriteBuilder(OverwriteType.Role, roleId).apply(builder).toOverwrite()

    return addOverwrite(overwrite)
}
