package dev.kord.rest.builder.channel

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.ChannelModifyPatchRequest

@KordDsl
public class CategoryModifyBuilder : PermissionOverwritesModifyBuilder, AuditRequestBuilder<ChannelModifyPatchRequest> {

    override var reason: String? = null

    private var _name: Optional<String> = Optional.Missing()

    /**
     * The name of the category.
     */
    public var name: String? by ::_name.delegate()

    private var _position: OptionalInt? = OptionalInt.Missing

    /**
     * The position of this category in the guild's channel list.
     */
    public var position: Int? by ::_position.delegate()

    private var _permissionOverwrites: Optional<MutableSet<Overwrite>?> = Optional.Missing()

    /**
     *  The permission overwrites for this category.
     */
    override var permissionOverwrites: MutableSet<Overwrite>? by ::_permissionOverwrites.delegate()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
        name = _name,
        position = _position,
        permissionOverwrites = _permissionOverwrites
    )
}
