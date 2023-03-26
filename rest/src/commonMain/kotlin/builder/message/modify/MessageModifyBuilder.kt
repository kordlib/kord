package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.DiscordAttachment
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import io.ktor.client.request.forms.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
public sealed interface MessageModifyBuilder {

    public var content: String?

    public var embeds: MutableList<EmbedBuilder>?

    public var allowedMentions: AllowedMentionsBuilder?

    public var components: MutableList<MessageComponentBuilder>?


    /**
     * The files to include as attachments
     */
    public var files: MutableList<NamedFile>?

    public var attachments: MutableList<DiscordAttachment>?

    /**
     * Optional custom [MessageFlags] to update in this message.
     *
     * @see suppressEmbeds
     */
    public var flags: MessageFlags?

    /**
     * Do not include any embeds when serializing this message.
     */
    public var suppressEmbeds: Boolean?

    /**
     * Adds a file with the [name] and [contentProvider] to the attachments.
     */
    public fun addFile(name: String, contentProvider: ChannelProvider): NamedFile =
        NamedFile(name, contentProvider).also { file ->
            files = (files ?: mutableListOf()).also {
                it.add(file)
            }
        }
}

public inline fun MessageModifyBuilder.embed(block: EmbedBuilder.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    embeds = (embeds ?: mutableListOf()).also {
        it.add(EmbedBuilder().apply(block))
    }
}

/**
 * Configures the mentions that should trigger a ping. Not calling this function will result in the default behavior
 * (ping everything), calling this function but not configuring it before the request is build will result in all
 * pings being ignored.
 */
public inline fun MessageModifyBuilder.allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
}


public inline fun MessageModifyBuilder.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    components = (components ?: mutableListOf()).also {
        it.add(ActionRowBuilder().apply(builder))
    }
}

/**
 * Sets/Unsets the [MessageFlags] for this message.
 *
 * **Only supports [MessageFlag.SuppressEmbeds]**
 */
public inline fun MessageModifyBuilder.messageFlags(builder: MessageFlags.Builder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    flags = MessageFlags(builder)
}
