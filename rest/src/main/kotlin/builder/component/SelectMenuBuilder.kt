package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A builder for a
 * [Discord Select Menu](https://discord.com/developers/docs/interactions/message-components#select-menus).
 *
 * @param customId The identifier for the menu, max 100 characters.
 */
@KordDsl
class SelectMenuBuilder(
    var customId: String
) : ActionRowComponentBuilder {

    private var _disabled: OptionalBoolean = OptionalBoolean.Missing

    /**
     * if this select menu is disabled.
     */
    var disabled: Boolean? by ::_disabled.delegate()

    /**
     * The choices in the select, max 25.
     */
    val options: MutableList<SelectOptionBuilder> = mutableListOf()

    /**
     * The range of values that can be accepted. Accepts any range between [0,25].
     * Defaults to `1..1`.
     */
    var allowedValues: IntRange = 1..1


    private var _placeholder: Optional<String> = Optional.Missing()

    /**
     * Custom placeholder if no value is selected, max 100 characters.
     *
     * [Option defaults][SelectOptionBuilder.default] have priority over placeholders,
     * if any option is marked as default then that label will be shown instead.
     */
    var placeholder: String? by ::_placeholder.delegate()

    /**
     * Adds a new option to the select menu with the given [label] and [value] and configured by the [builder].
     *
     * @param label The user-facing name of the option, max 25 characters.
     * @param value The dev-define value of the option, max 100 characters.
     */
    inline fun option(label: String, value: String, builder: SelectOptionBuilder.() -> Unit = {}) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        options.add(SelectOptionBuilder(label = label, value = value).apply(builder))
    }

    override fun build(): DiscordComponent {
        return DiscordComponent(
            ComponentType.SelectMenu,
            customId = Optional(customId),
            disabled = _disabled,
            placeholder = _placeholder,
            minValues = OptionalInt.Value(allowedValues.first),
            maxValues = OptionalInt.Value(allowedValues.last),
            options = Optional(options.map { it.build() })
        )
    }

}
