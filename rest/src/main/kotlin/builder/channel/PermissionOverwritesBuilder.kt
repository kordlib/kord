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
interface PermissionOverwritesBuilder {
    /**
     * Adds [overwrite] to this builder.
     */
    fun addOverwrite(overwrite: Overwrite)
}

/**
 * [PermissionOverwritesBuilder] which creates an entity with overwrites.
 */
interface PermissionOverwritesCreateBuilder : PermissionOverwritesBuilder {
    var permissionOverwrites: MutableSet<Overwrite>

    override fun addOverwrite(overwrite: Overwrite) {
        permissionOverwrites.add(overwrite)
    }
}

/**
 * [PermissionOverwritesBuilder] which modifies an existing entity with overwrites.
 */
interface PermissionOverwritesModifyBuilder : PermissionOverwritesBuilder {
    var permissionOverwrites: MutableSet<Overwrite>?

    override fun addOverwrite(overwrite: Overwrite) {
        val overwrites = permissionOverwrites ?: mutableSetOf()
        overwrites.add(overwrite)

        permissionOverwrites = overwrites
    }
}

/**
 * Adds an [Overwrite] for the [memberId].
 */
@OptIn(ExperimentalContracts::class)
inline fun PermissionOverwritesBuilder.addMemberOverwrite(
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
@OptIn(ExperimentalContracts::class)
inline fun PermissionOverwritesBuilder.addRoleOverwrite(
    roleId: Snowflake,
    builder: PermissionOverwriteBuilder.() -> Unit
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    val overwrite = PermissionOverwriteBuilder(OverwriteType.Member, roleId).apply(builder).toOverwrite()

    return addOverwrite(overwrite)
}
