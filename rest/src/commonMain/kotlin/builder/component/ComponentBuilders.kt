@file:Suppress("PropertyName")

package dev.kord.rest.builder.component

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordComponent
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate

@KordDsl
public sealed interface ComponentBuilder {
    public fun build(): DiscordComponent
}

@KordDsl
public sealed class ActionRowComponentBuilder : ComponentBuilder {

    protected var _disabled: OptionalBoolean = OptionalBoolean.Missing
        private set

    /** Whether the component is disabled. Defaults to `false`. */
    public var disabled: Boolean? by ::_disabled.delegate()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ActionRowComponentBuilder

        return disabled == other.disabled
    }

    override fun hashCode(): Int {
        return disabled?.hashCode() ?: 0
    }

}

@KordDsl
public sealed interface MessageComponentBuilder : ComponentBuilder
