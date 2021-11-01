package dev.kord.rest.builder.channel

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Snowflake
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Builder that can modify [permission overwrites][Overwrite].
 */
interface PermissionOverritesBuilder {
    /**
     *  The permission overwrites for this category.
     */
    var permissionOverwrites: MutableSet<Overwrite>?
}

/**
 * adds a [Overwrite] for the [memberId].
 */
@OptIn(ExperimentalContracts::class)
inline fun PermissionOverritesBuilder.addMemberOverwrite(memberId: Snowflake, builder: PermissionOverwriteBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val overwrite = permissionOverwrites ?: mutableSetOf()
    overwrite.add(PermissionOverwriteBuilder(OverwriteType.Member, memberId).apply(builder).toOverwrite())
    permissionOverwrites = overwrite
}

/**
 * adds a [Overwrite] for the [roleId].
 */
@OptIn(ExperimentalContracts::class)
inline fun PermissionOverritesBuilder.addRoleOverwrite(roleId: Snowflake, builder: PermissionOverwriteBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val overwrite = permissionOverwrites ?: mutableSetOf()
    overwrite.add(PermissionOverwriteBuilder(OverwriteType.Role, roleId).apply(builder).toOverwrite())
    permissionOverwrites = overwrite
}
