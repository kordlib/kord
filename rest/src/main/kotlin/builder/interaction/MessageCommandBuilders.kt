package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest


@KordDsl
public interface MessageCommandModifyBuilder : ApplicationCommandModifyBuilder

@KordDsl
public interface GlobalMessageCommandModifyBuilder : MessageCommandModifyBuilder, GlobalApplicationCommandModifyBuilder

@PublishedApi
internal class MessageCommandModifyBuilderImpl : GlobalMessageCommandModifyBuilder {

    private val state = ApplicationCommandModifyStateHolder()

    override var name: String? by state::name.delegate()
    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()

    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()
    override var dmPermission: Boolean? by state::dmPermission.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    override var defaultPermission: Boolean? by state::defaultPermission.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest {
        return ApplicationCommandModifyRequest(
            name = state.name,
            nameLocalizations = state.nameLocalizations,
            dmPermission = state.dmPermission,
            defaultMemberPermissions = state.defaultMemberPermissions,
            defaultPermission = state.defaultPermission
        )

    }

}

@KordDsl
public interface MessageCommandCreateBuilder : ApplicationCommandCreateBuilder

@KordDsl
public interface GlobalMessageCommandCreateBuilder : MessageCommandCreateBuilder, GlobalApplicationCommandCreateBuilder

@PublishedApi
internal class MessageCommandCreateBuilderImpl(override var name: String) : GlobalMessageCommandCreateBuilder {
    override val type: ApplicationCommandType
        get() = ApplicationCommandType.Message


    private val state = ApplicationCommandModifyStateHolder()

    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()

    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()
    override var dmPermission: Boolean? by state::dmPermission.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    override var defaultPermission: Boolean? by state::defaultPermission.delegate()

    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(
            name = name,
            nameLocalizations = state.nameLocalizations,
            type = type,
            dmPermission = state.dmPermission,
            defaultMemberPermissions = state.defaultMemberPermissions,
            defaultPermission = state.defaultPermission
        )
    }
}
