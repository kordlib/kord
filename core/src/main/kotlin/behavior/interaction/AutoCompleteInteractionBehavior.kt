package dev.kord.core.behavior.interaction

import dev.kord.common.entity.Choice
import dev.kord.common.entity.DiscordAutoComplete
import dev.kord.rest.builder.interaction.IntChoiceBuilder
import dev.kord.rest.builder.interaction.NumberChoiceBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Behavior of an AutoComplete interaction.
 *
 * @see respondNumber
 * @see respondString
 * @see respondInt
 * @see respond
 */
public interface AutoCompleteInteractionBehavior : InteractionBehavior

/**
 * Responds with the int choices specified by [builder].
 *
 * @see IntChoiceBuilder
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun AutoCompleteInteractionBehavior.respondInt(builder: IntChoiceBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    kord.rest.interaction.createIntAutoCompleteInteractionResponse(id, token, builder)
}

/**
 * Responds with the number choices specified by [builder].
 *
 * @see NumberChoiceBuilder
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun AutoCompleteInteractionBehavior.respondNumber(builder: NumberChoiceBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    kord.rest.interaction.createNumberAutoCompleteInteractionResponse(id, token, builder)
}

/**
 * Responds with the string choices specified by [builder].
 *
 * @see StringChoiceBuilder
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun AutoCompleteInteractionBehavior.respondString(builder: StringChoiceBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    kord.rest.interaction.createStringAutoCompleteInteractionResponse(id, token, builder)
}

/**
 * Responds with [choices] to this auto-complete request.
 */
public suspend inline fun <reified T> AutoCompleteInteractionBehavior.respond(choices: List<Choice<T>>) {
    kord.rest.interaction.createAutoCompleteInteractionResponse(
        id,
        token,
        DiscordAutoComplete(choices)
    )
}
