package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordPreview
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The base builder for creating a new message.
 */
sealed interface MessageCreateBuilder {

    /**
     * The text content of the message.
     */
    var content: String?

    /**
     * Whether this message should be played as a text-to-speech message.
     */
    var tts: Boolean?

    /**
     * The embedded content of the message.
     */
    val embeds: MutableList<EmbedBuilder>

    /**
     * The mentions in this message that are allowed to raise a notification.
     * Setting this to null will default to creating notifications for all mentions.
     */
    var allowedMentions: AllowedMentionsBuilder?

    /**
     * The message components to include in this message.
     */

    val components: MutableList<MessageComponentBuilder>

}

/**
 * Adds an embed to the message, configured by the [block]. A message can have up to 10 embeds.
 */
@OptIn(ExperimentalContracts::class)
inline fun MessageCreateBuilder.embed(block: EmbedBuilder.() -> Unit) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    embeds.add(EmbedBuilder().apply(block))
}

/**
 * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
 * (ping everything), calling this function but not configuring it before the request is build will result in all
 * pings being ignored.
 */
@OptIn(ExperimentalContracts::class)
inline fun MessageCreateBuilder.allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
}

/**
 * Adds an Action Row to the message, configured by the [builder]. A message can have up to 5 action rows.
 */
@OptIn(ExperimentalContracts::class)

inline fun MessageCreateBuilder.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    components.add(ActionRowBuilder().apply(builder))
}
