package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationCommandType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapList
import dev.kord.rest.json.request.ApplicationCommandCreateRequest
import dev.kord.rest.json.request.ApplicationCommandModifyRequest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@KordDsl
public sealed interface BaseInputChatBuilder {
    public var options: MutableList<OptionsBuilder>?

}

public inline fun BaseInputChatBuilder.mentionable(
    name: String,
    description: String,
    builder: MentionableBuilder.() -> Unit = {}
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(MentionableBuilder(name, description).apply(builder))

}

public inline fun BaseInputChatBuilder.channel(
    name: String,
    description: String,
    builder: ChannelBuilder.() -> Unit = {}
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(ChannelBuilder(name, description).apply(builder))
}

public inline fun BaseInputChatBuilder.user(name: String, description: String, builder: UserBuilder.() -> Unit = {}) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(UserBuilder(name, description).apply(builder))
}

public inline fun BaseInputChatBuilder.role(name: String, description: String, builder: RoleBuilder.() -> Unit = {}) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(RoleBuilder(name, description).apply(builder))
}

public inline fun BaseInputChatBuilder.attachment(
    name: String,
    description: String,
    builder: AttachmentBuilder.() -> Unit = {}
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(AttachmentBuilder(name, description).apply(builder))
}

public inline fun BaseInputChatBuilder.number(
    name: String,
    description: String,
    builder: NumberOptionBuilder.() -> Unit = {}
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(NumberOptionBuilder(name, description).apply(builder))
}

public inline fun BaseInputChatBuilder.string(
    name: String,
    description: String,
    builder: StringChoiceBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(StringChoiceBuilder(name, description).apply(builder))
}

public inline fun BaseInputChatBuilder.int(
    name: String,
    description: String,
    builder: IntegerOptionBuilder.() -> Unit = {}
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(IntegerOptionBuilder(name, description).apply(builder))
}

public inline fun BaseInputChatBuilder.boolean(
    name: String,
    description: String,
    builder: BooleanBuilder.() -> Unit = {}
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(BooleanBuilder(name, description).apply(builder))
}

@KordDsl
public interface RootInputChatBuilder : BaseInputChatBuilder

public inline fun RootInputChatBuilder.subCommand(
    name: String,
    description: String,
    builder: SubCommandBuilder.() -> Unit = {}
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(SubCommandBuilder(name, description).apply(builder))
}

public inline fun RootInputChatBuilder.group(
    name: String,
    description: String,
    builder: GroupCommandBuilder.() -> Unit = {}
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    if (options == null) options = mutableListOf()
    options!!.add(GroupCommandBuilder(name, description).apply(builder))
}

@KordDsl
public interface ChatInputCreateBuilder : LocalizedDescriptionCreateBuilder, ApplicationCommandCreateBuilder,
    RootInputChatBuilder

@KordDsl
public interface GlobalChatInputCreateBuilder : ChatInputCreateBuilder, GlobalApplicationCommandCreateBuilder

@PublishedApi
internal class ChatInputCreateBuilderImpl(
    override var name: String,
    override var description: String,
) : GlobalChatInputCreateBuilder {
    private val state = ApplicationCommandModifyStateHolder()

    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()
    override var descriptionLocalizations: MutableMap<Locale, String>? by state::descriptionLocalizations.delegate()

    override val type: ApplicationCommandType
        get() = ApplicationCommandType.ChatInput

    override var options: MutableList<OptionsBuilder>? by state::options.delegate()
    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()
    override var dmPermission: Boolean? by state::dmPermission.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    override var defaultPermission: Boolean? by state::defaultPermission.delegate()


    override fun toRequest(): ApplicationCommandCreateRequest {
        return ApplicationCommandCreateRequest(
            name,
            state.nameLocalizations,
            type,
            Optional.Value(description),
            state.descriptionLocalizations,
            state.options.mapList { it.toRequest() },
            state.defaultMemberPermissions,
            state.dmPermission,
            state.defaultPermission
        )

    }

}

@KordDsl
public interface ChatInputModifyBuilder : LocalizedDescriptionModifyBuilder, ApplicationCommandModifyBuilder,
    RootInputChatBuilder

@KordDsl
public interface GlobalChatInputModifyBuilder : ChatInputModifyBuilder, GlobalApplicationCommandModifyBuilder

@PublishedApi
internal class ChatInputModifyBuilderImpl : GlobalChatInputModifyBuilder {

    private val state = ApplicationCommandModifyStateHolder()
    override var name: String? by state::name.delegate()
    override var nameLocalizations: MutableMap<Locale, String>? by state::nameLocalizations.delegate()

    override var description: String? by state::description.delegate()
    override var descriptionLocalizations: MutableMap<Locale, String>? by state::descriptionLocalizations.delegate()

    override var options: MutableList<OptionsBuilder>? by state::options.delegate()

    override var defaultMemberPermissions: Permissions? by state::defaultMemberPermissions.delegate()
    override var dmPermission: Boolean? by state::dmPermission.delegate()

    @Deprecated("'defaultPermission' is deprecated in favor of 'defaultMemberPermissions' and 'dmPermission'. Setting 'defaultPermission' to false can be replaced by setting 'defaultMemberPermissions' to empty Permissions and 'dmPermission' to false ('dmPermission' is only available for global commands).")
    override var defaultPermission: Boolean? by state::defaultPermission.delegate()

    override fun toRequest(): ApplicationCommandModifyRequest {
        return ApplicationCommandModifyRequest(
            state.name,
            state.nameLocalizations,
            state.description,
            state.descriptionLocalizations,
            state.options.mapList { it.toRequest() },
            state.defaultMemberPermissions,
            state.dmPermission,
            state.defaultPermission
        )

    }

}
