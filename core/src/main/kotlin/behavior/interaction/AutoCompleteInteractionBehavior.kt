package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Choice
import dev.kord.common.entity.DiscordAutoComplete
import dev.kord.core.entity.interaction.AutoCompleteInteraction
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.interaction.IntChoiceBuilder
import dev.kord.rest.builder.interaction.NumberChoiceBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of an [AutoCompleteInteraction].
 *
 * @see suggestString
 * @see suggestInt
 * @see suggestNumber
 * @see suggest
 */
public interface AutoCompleteInteractionBehavior : DataInteractionBehavior {

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): AutoCompleteInteractionBehavior
}

/**
 * Responds to the interaction with the int choices specified by [builder].
 *
 * The provided choices are only suggestions and the user can provide any other input as well.
 *
 * @see IntChoiceBuilder
 */
public suspend inline fun AutoCompleteInteractionBehavior.suggestInt(builder: IntChoiceBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    kord.rest.interaction.createIntAutoCompleteInteractionResponse(id, token, builder)
}

/**
 * Responds to the interaction with the number choices specified by [builder].
 *
 * The provided choices are only suggestions and the user can provide any other input as well.
 *
 * @see NumberChoiceBuilder
 */
public suspend inline fun AutoCompleteInteractionBehavior.suggestNumber(builder: NumberChoiceBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    kord.rest.interaction.createNumberAutoCompleteInteractionResponse(id, token, builder)
}

/**
 * Responds to the interaction with the string choices specified by [builder].
 *
 * The provided choices are only suggestions and the user can provide any other input as well.
 *
 * @see StringChoiceBuilder
 */
public suspend inline fun AutoCompleteInteractionBehavior.suggestString(builder: StringChoiceBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    kord.rest.interaction.createStringAutoCompleteInteractionResponse(id, token, builder)
}

/**
 * Responds to the interaction with [choices] to this auto-complete request.
 *
 * The provided choices are only suggestions and the user can provide any other input as well.
 */
public suspend inline fun <reified T> AutoCompleteInteractionBehavior.suggest(choices: List<Choice<T>>) {
    kord.rest.interaction.createAutoCompleteInteractionResponse(
        id,
        token,
        DiscordAutoComplete(choices)
    )
}
