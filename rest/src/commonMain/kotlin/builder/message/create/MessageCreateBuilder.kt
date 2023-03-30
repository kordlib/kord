package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import io.ktor.client.request.forms.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The base builder for creating a new message.
 */
@KordDsl
public sealed interface MessageCreateBuilder {

    /**
     * The text content of the message.
     */
    public var content: String?

    /**
     * Whether this message should be played as a text-to-speech message.
     */
    public var tts: Boolean?

    /**
     * The embedded content of the message.
     */
    public val embeds: MutableList<EmbedBuilder>

    /**
     * The mentions in this message that are allowed to raise a notification.
     * Setting this to null will default to creating notifications for all mentions.
     */
    public var allowedMentions: AllowedMentionsBuilder?

    /**
     * The message components to include in this message.
     */

    public val components: MutableList<MessageComponentBuilder>

    /**
     * The files to include as attachments.
     */
    public val files: MutableList<NamedFile>

    /**
     * Optional custom [MessageFlags] to add to the message created.
     *
     * @see suppressEmbeds
     * @see suppressNotifications
     */
    public var flags: MessageFlags?

    /**
     * Do not include any embeds when serializing this message.
     */
    public var suppressEmbeds: Boolean?

    /**
     * This message will not trigger push and desktop notifications.
     */
    public var suppressNotifications: Boolean?

    /**
     * Adds a file with the [name] and [contentProvider] to the attachments.
     */
    public fun addFile(name: String, contentProvider: ChannelProvider): NamedFile {
        val namedFile = NamedFile(name, contentProvider)
        files += namedFile
        return namedFile
    }
}

internal fun buildMessageFlags(
    base: MessageFlags?,
    suppressEmbeds: Boolean?,
    suppressNotifications: Boolean? = null,
    ephemeral: Boolean? = null
): Optional<MessageFlags> {
    fun MessageFlags.Builder.add(add: Boolean?, flag: MessageFlag) {
        when (add) {
            true -> +flag
            false -> -flag
            null -> {}
        }
    }

    if (base == null && suppressEmbeds == null && suppressNotifications == null && ephemeral == null) {
        return Optional.Missing()
    }

    val flags = MessageFlags {
        if (base != null) +base
        add(suppressEmbeds, MessageFlag.SuppressEmbeds)
        add(suppressNotifications, MessageFlag.SuppressNotifications)
        add(ephemeral, MessageFlag.Ephemeral)
    }

    return Optional.Value(flags)
}

/**
 * Adds an embed to the message, configured by the [block]. A message can have up to 10 embeds.
 */
public inline fun MessageCreateBuilder.embed(block: EmbedBuilder.() -> Unit) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    embeds.add(EmbedBuilder().apply(block))
}

/**
 * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
 * (ping everything), calling this function but not configuring it before the request is build will result in all
 * pings being ignored.
 */
public inline fun MessageCreateBuilder.allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
}

/**
 * Adds an Action Row to the message, configured by the [builder]. A message can have up to 5 action rows.
 */
public inline fun MessageCreateBuilder.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    components.add(ActionRowBuilder().apply(builder))
}

/**
 * Sets the [MessageFlags] for the created message.
 *
 * **Only supports [MessageFlag.SuppressEmbeds] and [MessageFlag.SuppressNotifications]**
 */
public inline fun MessageCreateBuilder.messageFlags(builder: MessageFlags.Builder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    flags = MessageFlags(builder)
}
