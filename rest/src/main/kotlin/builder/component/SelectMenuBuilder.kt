package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.ComponentType
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.delegate.delegate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A builder for a
 * [Discord Select Menu](https://discord.com/developers/docs/interactions/message-components#select-menus).
 *
 * @param customId The identifier for the menu, max 100 characters.
 */
@KordPreview
class SelectMenuBuilder(
    var customId: String
) : ActionRowComponentBuilder {

    /**
     * The choices in the select, max 25.
     */
    val options: MutableList<SelectOptionBuilder> = mutableListOf()

    /**
     * The minimum amount of values that can be accepted. Accepts a range of `[0,25]`.
     * Defaults to `1`.
     *
     * If a value is set that is bigger than [maximumValues], then [maximumValues] will
     * be updated to that new value.
     *
     * Setting [minimumValues] to a number higher than the amount of possible options is not allowed.
     */
    var minimumValues: Int = 1
        set(value) {
            field = value
            if (value > maximumValues) {
                maximumValues = value
            }
        }

    /**
     * The maximum amount of values that can be accepted. Accepts a range of `[0,25]`.
     * Defaults to `1`.
     *
     * If a value is set that is smaller than [minimumValues], then [minimumValues] will
     * be updated to that new value.
     *
     * Setting [maximumValues] to a number higher than the amount of possible options is not allowed.
     */
    var maximumValues: Int = 1
        set(value) {
            field = value
            if (value < minimumValues) {
                minimumValues = value
            }
        }

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
    @OptIn(ExperimentalContracts::class)
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
            placeholder = _placeholder,
            minValues = OptionalInt.Value(minimumValues),
            maxValues = OptionalInt.Value(maximumValues),
            options = Optional(options.map { it.build() })
        )
    }

}
