package dev.kord.rest.builder.channel

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Overwrite
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.GuildChannelCreateRequest
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
class CategoryCreateBuilder(var name: String) : AuditRequestBuilder<GuildChannelCreateRequest> {
    override var reason: String? = null

    private var _position: OptionalInt = OptionalInt.Missing
    var position: Int? by ::_position.delegate()

    private var _nsfw: OptionalBoolean = OptionalBoolean.Missing
    var nsfw: Boolean? by ::_nsfw.delegate()

    val permissionOverwrites: MutableList<Overwrite> = mutableListOf()

    /**
     * adds a [Overwrite] for the [memberId].
     */
    @OptIn(ExperimentalContracts::class)
    inline fun addMemberOverwrite(memberId: Snowflake, builder: PermissionOverwriteBuilder.() -> Unit){
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        permissionOverwrites += PermissionOverwriteBuilder(OverwriteType.Member, memberId).apply(builder).toOverwrite()
    }

    /**
     * adds a [Overwrite] for the [roleId].
     */
    @OptIn(ExperimentalContracts::class)
    inline fun addRoleOverwrite(roleId: Snowflake, builder: PermissionOverwriteBuilder.() -> Unit){
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        permissionOverwrites += PermissionOverwriteBuilder(OverwriteType.Role, roleId).apply(builder).toOverwrite()
    }

    override fun toRequest(): GuildChannelCreateRequest = GuildChannelCreateRequest(
            name = name,
            position = _position,
            nsfw = _nsfw,
            permissionOverwrite = Optional.missingOnEmpty(permissionOverwrites),
            type = ChannelType.GuildCategory
    )
}
