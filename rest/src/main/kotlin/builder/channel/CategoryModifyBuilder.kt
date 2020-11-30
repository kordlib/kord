package dev.kord.rest.builder.channel

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.ChannelModifyPatchRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
class CategoryModifyBuilder: AuditRequestBuilder<ChannelModifyPatchRequest> {

    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()
    /**
     * The name of the category.
     */
    var name: String? by ::_name.delegate()

    private var _position: OptionalInt = OptionalInt.Missing

    /**
     * The position of this category in the guild's channel list.
     */
    var position: Int? by ::_position.delegate()

    /**
     *  The permission overwrites for this category.
     */
    var permissionOverwrites: MutableSet<Overwrite> = mutableSetOf()

    /**
     * adds a [Overwrite] for the [memberId].
     */
    @OptIn(ExperimentalContracts::class)
    inline fun addMemberOverwrite(memberId: Snowflake, builder: PermissionOverwriteBuilder.() -> Unit){
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        permissionOverwrites.add(PermissionOverwriteBuilder(OverwriteType.Member, memberId).apply(builder).toOverwrite())
    }

    /**
     * adds a [Overwrite] for the [roleId].
     */
    @OptIn(ExperimentalContracts::class)
    inline fun addRoleOverwrite(roleId: Snowflake, builder: PermissionOverwriteBuilder.() -> Unit){
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        permissionOverwrites.add(PermissionOverwriteBuilder(OverwriteType.Role, roleId).apply(builder).toOverwrite())
    }

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = _name,
            position = _position,
            permissionOverwrites = Optional.missingOnEmpty(permissionOverwrites)
    )
}