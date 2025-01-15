package dev.kord.rest.builder.role

import dev.kord.common.Color
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.rest.Image
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.json.request.GuildRoleCreateRequest

@KordDsl
public class RoleCreateBuilder : AuditRequestBuilder<GuildRoleCreateRequest> {
    override var reason: String? = null

    private var _color: Optional<Color> = Optional.Missing()

    /** Color of the role, no color by default. */
    public var color: Color? by ::_color.delegate()

    private var _hoist: OptionalBoolean = OptionalBoolean.Missing

    /** Whether the role should be displayed separately in the sidebar, `false` by default. */
    public var hoist: Boolean? by ::_hoist.delegate()

    private var _icon: Optional<Image?> = Optional.Missing()

    /** The role's icon image (if the guild has the [RoleIcons][GuildFeature.RoleIcons] feature), `null` by default. */
    public var icon: Image? by ::_icon.delegate()

    private var _unicodeEmoji: Optional<String?> = Optional.Missing()

    /**
     * The role's unicode emoji as a [standard emoji](https://discord.com/developers/docs/reference#message-formatting)
     * (if the guild has the [RoleIcons][GuildFeature.RoleIcons] feature), `null` by default.
     */
    public var unicodeEmoji: String? by ::_unicodeEmoji.delegate()

    private var _name: Optional<String> = Optional.Missing()

    /** Name of the role, "new role" by default. */
    public var name: String? by ::_name.delegate()

    private var _mentionable: OptionalBoolean = OptionalBoolean.Missing

    /** Whether the role should be mentionable, `false` by default. */
    public var mentionable: Boolean? by ::_mentionable.delegate()

    private var _permissions: Optional<Permissions> = Optional.Missing()

    /** Permissions for the role, @everyone permissions in the guild by default. */
    public var permissions: Permissions? by ::_permissions.delegate()

    override fun toRequest(): GuildRoleCreateRequest = GuildRoleCreateRequest(
        color = _color,
        hoist = _hoist,
        icon = _icon.map { it.dataUri },
        unicodeEmoji = _unicodeEmoji,
        name = _name,
        mentionable = _mentionable,
        permissions = _permissions
    )
}
