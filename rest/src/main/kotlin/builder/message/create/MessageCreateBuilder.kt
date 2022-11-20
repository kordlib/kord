package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import io.ktor.client.request.forms.*
import io.ktor.util.cio.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.InputStream
import java.nio.file.Path
import kotlin.DeprecationLevel.ERROR
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
     * Adds a file with the [name] and [content] to the attachments.
     *
     * @suppress
     */
    @Deprecated(
        "Use lazy ChannelProvider instead of InputStream. You should also make sure that the stream/channel is only " +
                "opened inside the block of the ChannelProvider because it could otherwise be read multiple times " +
                "(which isn't allowed).",
        ReplaceWith(
            "addFile(name, ChannelProvider { content.toByteReadChannel() })",
            "io.ktor.client.request.forms.ChannelProvider",
            "io.ktor.utils.io.jvm.javaio.toByteReadChannel",
        ),
        level = ERROR,
    )
    public fun addFile(name: String, content: InputStream): NamedFile =
        addFile(name, ChannelProvider { content.toByteReadChannel() })

    /**
     * Adds a file with the [name] and [contentProvider] to the attachments.
     */
    public fun addFile(name: String, contentProvider: ChannelProvider): NamedFile {
        val namedFile = NamedFile(name, contentProvider)
        files += namedFile
        return namedFile
    }

    /**
     * Adds a file with the given [path] to the attachments.
     */
    public suspend fun addFile(path: Path): NamedFile =
        addFile(path.fileName.toString(), ChannelProvider { path.readChannel() })
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
