@file:Suppress("PropertyName")

package dev.kord.rest.builder.component

import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate


sealed interface ComponentBuilder {
    fun build(): DiscordComponent
}


sealed class ActionRowComponentBuilder : ComponentBuilder {

    protected var _disabled: OptionalBoolean = OptionalBoolean.Missing
        private set

    /** Whether the component is disabled. Defaults to `false`. */
    var disabled: Boolean? by ::_disabled.delegate()
}


sealed interface MessageComponentBuilder : ComponentBuilder
