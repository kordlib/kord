package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.DiscordSelectOption
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
    final override fun build(): DiscordChatComponent = DiscordChatComponent(
        type = type,
        customId = Optional(customId),
        options = buildOptions(),
        channelTypes = buildChannelTypes(),
        placeholder = _placeholder,
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

/** The choices in the select, max 25. */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated("Replaced by member in StringSelectBuilder.", ReplaceWith("this.options"), DeprecationLevel.ERROR)
public var StringSelectBuilder.options: MutableList<SelectOptionBuilder>
    get() = options
    set(value) {
        options = value
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
}

@KordDsl
public class RoleSelectBuilder(customId: String) : SelectMenuBuilder(customId) {
    override val type: ComponentType get() = ComponentType.RoleSelect
}

@KordDsl
public class MentionableSelectBuilder(customId: String) : SelectMenuBuilder(customId) {
    override val type: ComponentType get() = ComponentType.MentionableSelect
}

@KordDsl
public class ChannelSelectBuilder(customId: String) : SelectMenuBuilder(customId) {
    override val type: ComponentType get() = ComponentType.ChannelSelect

    private var _channelTypes: Optional<MutableList<ChannelType>> = Optional.Missing()
    public var channelTypes: MutableList<ChannelType>? by ::_channelTypes.delegate()

    override fun buildChannelTypes(): Optional<List<ChannelType>> = _channelTypes.mapCopy()
}

public fun ChannelSelectBuilder.channelType(type: ChannelType) {
    channelTypes?.add(type) ?: run { channelTypes = mutableListOf(type) }
}
