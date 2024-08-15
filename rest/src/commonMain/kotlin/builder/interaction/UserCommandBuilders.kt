package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest

@KordDsl
public interface UserCommandModifyBuilder : ApplicationCommandModifyBuilder

@KordDsl
public interface GlobalUserCommandModifyBuilder : UserCommandModifyBuilder, GlobalApplicationCommandModifyBuilder

@PublishedApi
internal class UserCommandModifyBuilderImpl : GlobalUserCommandModifyBuilder {

    private val state = ApplicationCommandModifyStateHolder()

    override var name: String? by state::name.delegate()
    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()

    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()
    override var dmPermission: Boolean? by state::dmPermission.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    override var defaultPermission: Boolean? by @Suppress("DEPRECATION") state::defaultPermission.delegate()

    override var nsfw: Boolean? by state::nsfw.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest {
        return ApplicationCommandModifyRequest(
            name = state.name,
            nameLocalizations = state.nameLocalizations,
            dmPermission = state.dmPermission,
            defaultMemberPermissions = state.defaultMemberPermissions,
            defaultPermission = @Suppress("DEPRECATION") state.defaultPermission,
            nsfw = state.nsfw,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UserCommandModifyBuilderImpl

        if (state != other.state) return false
        if (name != other.name) return false
        if (nameLocalizations != other.nameLocalizations) return false
        if (defaultMemberPermissions != other.defaultMemberPermissions) return false
        if (dmPermission != other.dmPermission) return false
        if (nsfw != other.nsfw) return false

        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (nameLocalizations?.hashCode() ?: 0)
        result = 31 * result + (defaultMemberPermissions?.hashCode() ?: 0)
        result = 31 * result + (dmPermission?.hashCode() ?: 0)
        result = 31 * result + (nsfw?.hashCode() ?: 0)
        return result
    }

}

@KordDsl
public interface UserCommandCreateBuilder : ApplicationCommandCreateBuilder

@KordDsl
public interface GlobalUserCommandCreateBuilder : UserCommandCreateBuilder, GlobalApplicationCommandCreateBuilder

@PublishedApi
internal class UserCommandCreateBuilderImpl(override var name: String) : GlobalUserCommandCreateBuilder {
    override val type: ApplicationCommandType
        get() = ApplicationCommandType.User
    private val state = ApplicationCommandModifyStateHolder()

    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()

    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()
    override var dmPermission: Boolean? by state::dmPermission.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    override var defaultPermission: Boolean? by @Suppress("DEPRECATION") state::defaultPermission.delegate()

    override var nsfw: Boolean? by state::nsfw.delegate()

    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(
            name = name,
            nameLocalizations = state.nameLocalizations,
            type = type,
            defaultMemberPermissions = state.defaultMemberPermissions,
            dmPermission = state.dmPermission,
            defaultPermission = @Suppress("DEPRECATION") state.defaultPermission,
            nsfw = state.nsfw,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UserCommandCreateBuilderImpl

        if (name != other.name) return false
        if (type != other.type) return false
        if (state != other.state) return false
        if (nameLocalizations != other.nameLocalizations) return false
        if (defaultMemberPermissions != other.defaultMemberPermissions) return false
        if (dmPermission != other.dmPermission) return false
        if (nsfw != other.nsfw) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + (nameLocalizations?.hashCode() ?: 0)
        result = 31 * result + (defaultMemberPermissions?.hashCode() ?: 0)
        result = 31 * result + (dmPermission?.hashCode() ?: 0)
        result = 31 * result + (nsfw?.hashCode() ?: 0)
        return result
    }

}
