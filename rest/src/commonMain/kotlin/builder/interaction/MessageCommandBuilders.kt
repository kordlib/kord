package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.InteractionContextType
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
    @Deprecated("'dmPermission' is deprecated in favor of 'context'.")
    override var dmPermission: Boolean? by @Suppress("DEPRECATION") state::dmPermission.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    override var defaultPermission: Boolean? by @Suppress("DEPRECATION") state::defaultPermission.delegate()

    override var nsfw: Boolean? by state::nsfw.delegate()

    override var contexts: MutableList<InteractionContextType>? by state::contexts.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest {
        return ApplicationCommandModifyRequest(
            name = state.name,
            nameLocalizations = state.nameLocalizations,
            dmPermission = @Suppress("DEPRECATION") state.dmPermission,
            defaultMemberPermissions = state.defaultMemberPermissions,
            defaultPermission = @Suppress("DEPRECATION") state.defaultPermission,
            nsfw = state.nsfw,
            contexts = state.contexts
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
    @Deprecated("'dmPermission' is deprecated in favor of 'contexts'. Setting 'dmPermission' to false can be replaced by setting 'contexts' to empty InteractionContextType ('contexts' is only available for global commands).")
    override var dmPermission: Boolean? by @Suppress("DEPRECATION") state::dmPermission.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'contexts'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'contexts' to empty InteractionContextType ('contexts' is only available for global commands).")
    override var defaultPermission: Boolean? by @Suppress("DEPRECATION") state::defaultPermission.delegate()

    override var nsfw: Boolean? by state::nsfw.delegate()

    override var contexts: MutableList<InteractionContextType>? by state::contexts.delegate()

    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(
            name = name,
            nameLocalizations = state.nameLocalizations,
            type = type,
            dmPermission = @Suppress("DEPRECATION") state.dmPermission,
            defaultMemberPermissions = state.defaultMemberPermissions,
            defaultPermission = @Suppress("DEPRECATION") state.defaultPermission,
            nsfw = state.nsfw,
            contexts = state.contexts
        )
    }

}
