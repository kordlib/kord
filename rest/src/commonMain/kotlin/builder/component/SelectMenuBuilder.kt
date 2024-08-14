package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.*
import dev.kord.common.entity.SelectDefaultValueType.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapCopy
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A builder for a
 * [Discord Select Menu](https://discord.com/developers/docs/interactions/message-components#select-menus).
 *
 * @param customId The identifier for the menu, max 100 characters.
 */
@KordDsl
public sealed class SelectMenuBuilder(public var customId: String) : ActionRowComponentBuilder() {

    /**
     * The range of values that can be accepted. Accepts any range between [0,25].
     * Defaults to `1..1`.
     */
    public var allowedValues: ClosedRange<Int> = 1..1


    private var _placeholder: Optional<String> = Optional.Missing()

    /**
     * Custom placeholder if no value is selected, max 150 characters.
     *
     * [Option defaults][SelectOptionBuilder.default] have priority over placeholders,
     * if any option is marked as default then that label will be shown instead.
     */
    public var placeholder: String? by ::_placeholder.delegate()

    protected abstract val type: ComponentType
    protected open fun buildOptions(): Optional<List<DiscordSelectOption>> = Optional.Missing()
    protected open fun buildChannelTypes(): Optional<List<ChannelType>> = Optional.Missing()
    protected open fun buildDefaultValues(): Optional<List<DiscordSelectDefaultValue>> = Optional.Missing()
    final override fun build(): DiscordChatComponent = DiscordChatComponent(
        type = type,
        customId = Optional(customId),
        options = buildOptions(),
        channelTypes = buildChannelTypes(),
        placeholder = _placeholder,
        defaultValues = buildDefaultValues(),
        minValues = OptionalInt.Value(allowedValues.start),
        maxValues = OptionalInt.Value(allowedValues.endInclusive),
        disabled = _disabled,
    )
}

@KordDsl
public class StringSelectBuilder(customId: String) : SelectMenuBuilder(customId) {
    override val type: ComponentType get() = ComponentType.StringSelect

    /** The choices in the select, max 25. */
    public var options: MutableList<SelectOptionBuilder> = mutableListOf()

    override fun buildOptions(): Optional<List<DiscordSelectOption>> = Optional(options.map { it.build() })
}

/**
 * Adds a new option to the select menu with the given [label] and [value] that can be configured by the [builder].
 *
 * @param label The user-facing name of the option, max 100 characters.
 * @param value The dev-defined value of the option, max 100 characters.
 */
public inline fun StringSelectBuilder.option(
    label: String,
    value: String,
    builder: SelectOptionBuilder.() -> Unit = {},
) {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    options.add(SelectOptionBuilder(label = label, value = value).apply(builder))
}

@KordDsl
public class UserSelectBuilder(customId: String) : SelectMenuBuilder(customId) {
    override val type: ComponentType get() = ComponentType.UserSelect

    /**
     * The list of default user IDs for this [user select menu][ComponentType.UserSelect].
     *
     * The number of default values must be in the range defined by [allowedValues].
     */
    public val defaultUsers: MutableList<Snowflake> = mutableListOf()

    override fun buildDefaultValues(): Optional<List<DiscordSelectDefaultValue>> =
        Optional.missingOnEmpty(defaultUsers.map { id -> DiscordSelectDefaultValue(id, type = User) })
}

@KordDsl
public class RoleSelectBuilder(customId: String) : SelectMenuBuilder(customId) {
    override val type: ComponentType get() = ComponentType.RoleSelect

    /**
     * The list of default role IDs for this [role select menu][ComponentType.RoleSelect].
     *
     * The number of default values must be in the range defined by [allowedValues].
     */
    public val defaultRoles: MutableList<Snowflake> = mutableListOf()

    override fun buildDefaultValues(): Optional<List<DiscordSelectDefaultValue>> =
        Optional.missingOnEmpty(defaultRoles.map { id -> DiscordSelectDefaultValue(id, type = Role) })
}

@KordDsl
public class MentionableSelectBuilder(customId: String) : SelectMenuBuilder(customId) {
    override val type: ComponentType get() = ComponentType.MentionableSelect

    /**
     * The list of default user IDs for this [mentionable select menu][ComponentType.MentionableSelect].
     *
     * The number of default values must be in the range defined by [allowedValues].
     */
    public val defaultUsers: MutableList<Snowflake> = mutableListOf()

    /**
     * The list of default role IDs for this [mentionable select menu][ComponentType.MentionableSelect].
     *
     * The number of default values must be in the range defined by [allowedValues].
     */
    public val defaultRoles: MutableList<Snowflake> = mutableListOf()

    override fun buildDefaultValues(): Optional<List<DiscordSelectDefaultValue>> = Optional.missingOnEmpty(
        defaultUsers.map { id -> DiscordSelectDefaultValue(id, type = User) } +
            defaultRoles.map { id -> DiscordSelectDefaultValue(id, type = Role) }
    )
}

@KordDsl
public class ChannelSelectBuilder(customId: String) : SelectMenuBuilder(customId) {
    override val type: ComponentType get() = ComponentType.ChannelSelect

    private var _channelTypes: Optional<MutableList<ChannelType>> = Optional.Missing()
    public var channelTypes: MutableList<ChannelType>? by ::_channelTypes.delegate()

    /**
     * The list of default channel IDs for this [channel select menu][ComponentType.ChannelSelect].
     *
     * The number of default values must be in the range defined by [allowedValues].
     */
    public val defaultChannels: MutableList<Snowflake> = mutableListOf()

    override fun buildChannelTypes(): Optional<List<ChannelType>> = _channelTypes.mapCopy()
    override fun buildDefaultValues(): Optional<List<DiscordSelectDefaultValue>> =
        Optional.missingOnEmpty(defaultChannels.map { id -> DiscordSelectDefaultValue(id, type = Channel) })
}

public fun ChannelSelectBuilder.channelType(type: ChannelType) {
    channelTypes?.add(type) ?: run { channelTypes = mutableListOf(type) }
}
