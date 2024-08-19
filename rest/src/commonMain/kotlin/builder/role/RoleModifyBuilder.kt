package dev.kord.rest.builder.role

import dev.kord.common.Color
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.GuildFeature
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildRoleModifyRequest

@KordDsl
public class RoleModifyBuilder : AuditRequestBuilder<GuildRoleModifyRequest> {
    override var reason: String? = null

    private var _color: Optional<Color?> = Optional.Missing()

    /** Color of the role. */
    public var color: Color? by ::_color.delegate()

    private var _hoist: OptionalBoolean? = OptionalBoolean.Missing

    /** Whether the role should be displayed separately in the sidebar. */
    public var hoist: Boolean? by ::_hoist.delegate()

    private var _icon: Optional<Image?> = Optional.Missing()

    /** The role's icon image (if the guild has the [RoleIcons][GuildFeature.RoleIcons] feature). */
    public var icon: Image? by ::_icon.delegate()

    private var _unicodeEmoji: Optional<String?> = Optional.Missing()

    /**
     * The role's unicode emoji as a [standard emoji](https://discord.com/developers/docs/reference#message-formatting)
     * (if the guild has the [RoleIcons][GuildFeature.RoleIcons] feature).
     */
    public var unicodeEmoji: String? by ::_unicodeEmoji.delegate()

    private var _name: Optional<String?> = Optional.Missing()

    /** Name of the role. */
    public var name: String? by ::_name.delegate()

    private var _mentionable: OptionalBoolean? = OptionalBoolean.Missing

    /** Whether the role should be mentionable. */
    public var mentionable: Boolean? by ::_mentionable.delegate()

    private var _permissions: Optional<Permissions?> = Optional.Missing()

    /** Permissions for the role. */
    public var permissions: Permissions? by ::_permissions.delegate()

    override fun toRequest(): GuildRoleModifyRequest = GuildRoleModifyRequest(
        name = _name,
        color = _color,
        hoist = _hoist,
        icon = _icon.map { it.dataUri },
        unicodeEmoji = _unicodeEmoji,
        mentionable = _mentionable,
        permissions = _permissions
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RoleModifyBuilder

        if (reason != other.reason) return false
        if (color != other.color) return false
        if (hoist != other.hoist) return false
        if (icon != other.icon) return false
        if (unicodeEmoji != other.unicodeEmoji) return false
        if (name != other.name) return false
        if (mentionable != other.mentionable) return false
        if (permissions != other.permissions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reason?.hashCode() ?: 0
        result = 31 * result + (color?.hashCode() ?: 0)
        result = 31 * result + (hoist?.hashCode() ?: 0)
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + (unicodeEmoji?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (mentionable?.hashCode() ?: 0)
        result = 31 * result + (permissions?.hashCode() ?: 0)
        return result
    }

}
