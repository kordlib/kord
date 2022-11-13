@file:Suppress("DEPRECATION")

package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordChatComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import kotlin.DeprecationLevel.WARNING
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A builder for a
 * [Discord Select Menu](https://discord.com/developers/docs/interactions/message-components#select-menus).
 *
 * @param customId The identifier for the menu, max 100 characters.
 */
@KordDsl
public open class SelectMenuBuilder
@Deprecated(
    "This will be made a sealed class in the future, please stop using this constructor. You can instead use the " +
            "constructor of one of the subtypes.",
    level = DeprecationLevel.WARNING,
)
public constructor(public var customId: String) : ActionRowComponentBuilder() {
    internal open val type: ComponentType get() = ComponentType.StringSelect

    /**
     * The choices in the select, max 25.
     *
     */
    @Deprecated(
        "Binary compatibility",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("dev.kord.rest.builder.component.StringSelectBuilder#options")
    )
    public open val options: MutableList<SelectOptionBuilder> = mutableListOf()

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

    /**
     * Adds a new option to the select menu with the given [label] and [value] and configured by the [builder].
     *
     * @param label The user-facing name of the option, max 100 characters.
     * @param value The dev-defined value of the option, max 100 characters.
     */
    @Deprecated(
        "This is only available for 'ComponentType.StringSelect' (in the 'StringSelectBuilder' subclass).",
        ReplaceWith(
            "(this as StringSelectBuilder).option(label, value, builder)",
            "dev.kord.rest.builder.component.StringSelectBuilder",
            "dev.kord.rest.builder.component.option",
        ),
        level = WARNING,
    )
    @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
    @kotlin.internal.LowPriorityInOverloadResolution
    public inline fun option(label: String, value: String, builder: SelectOptionBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        options.add(SelectOptionBuilder(label = label, value = value).apply(builder))
    }

    protected open fun buildChannelTypes(): Optional<List<ChannelType>> = Optional.Missing()

    override fun build(): DiscordChatComponent {
        return DiscordChatComponent(
            type,
            customId = Optional(customId),
            disabled = _disabled,
            placeholder = _placeholder,
            minValues = OptionalInt.Value(allowedValues.start),
            maxValues = OptionalInt.Value(allowedValues.endInclusive),
            options = Optional(options.map { it.build() }),
            channelTypes = buildChannelTypes()
        )
    }

}

@KordDsl
public class StringSelectBuilder(customId: String) : SelectMenuBuilder(customId) {
    override val type: ComponentType get() = ComponentType.StringSelect

    /**
     * The choices in the select, max 25.
     *
     */
    @Suppress("OVERRIDE_DEPRECATION")
    override val options: MutableList<SelectOptionBuilder> = mutableListOf()
}

/**
 * Adds a new option to the select menu with the given [label] and [value] that can be configured by the [builder].
 *
 * @param label The user-facing name of the option, max 100 characters.
 * @param value The dev-defined value of the option, max 100 characters.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER") // can be removed when member in SelectMenuBuilder is removed
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

    override fun buildChannelTypes(): Optional<List<ChannelType>> = _channelTypes
}
