package dev.kord.rest.builder.message

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.*
import dev.kord.rest.request.MultipartRequest
import io.ktor.client.request.forms.*
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

@KordDsl
public interface MessageBuilder : ComponentContainerBuilder {

    /** The message contents (up to 2000 characters). */
    public var content: String?

    /** Up to 10 embeds (up to 6000 characters). */
    public var embeds: MutableList<EmbedBuilder>?

    /**
     * The mentions in the message that are allowed to trigger a ping.
     *
     * Setting this to `null` will default to triggering pings for all mentions.
     */
    public var allowedMentions: AllowedMentionsBuilder?

    /** The components to include with the message.*/
    public var components: MutableList<MessageComponentBuilder>?

    /** The files to include as attachments. */
    public val files: MutableList<NamedFile>

    /**
     * The attachment objects with [filename][AttachmentBuilder.filename] and
     * [description][AttachmentBuilder.description].
     */
    public var attachments: MutableList<AttachmentBuilder>?

    /**
     * Optional custom [MessageFlags].
     *
     * @see suppressEmbeds
     */
    public var flags: MessageFlags?

    /** Do not include any embeds when serializing this message. */
    public var suppressEmbeds: Boolean?

    /** Adds a [file][NamedFile] with [name] and [contentProvider] to [files]. */
    public fun addFile(name: String, contentProvider: ChannelProvider): NamedFile {
        val file = NamedFile(name, contentProvider)
        files.add(file)
        return file
    }

    override fun addComponent(component: ContainerComponentBuilder) {
        components?.add(component) ?: run { components = mutableListOf(component) }
    }
}

/**
 * Adds an [embed][EmbedBuilder] configured by [builder] to the [embeds][MessageBuilder.embeds] of the message.
 *
 * A message can have up to 10 embeds.
 */
public inline fun MessageBuilder.embed(builder: EmbedBuilder.() -> Unit) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val embed = EmbedBuilder().apply(builder)
    embeds?.add(embed) ?: run { embeds = mutableListOf(embed) }
}

/**
 * Configures the mentions in the message that are allowed to trigger a ping.
 *
 * Not calling this function will result in the default behavior (ping for all mentions), calling this function but not
 * configuring it before the request is built will result in all mentions being ignored.
 */
public inline fun MessageBuilder.allowedMentions(builder: AllowedMentionsBuilder.() -> Unit = {}) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val mentions = allowedMentions ?: (AllowedMentionsBuilder().also { allowedMentions = it })
    mentions.builder()
}

/**
 * Adds an [action row][ActionRowBuilder] configured by the [builder] to the [components][MessageBuilder.components] of
 * the message.
 *
 * A message can have up to ten top-level components.
 */
@Deprecated("Use ComponentContainerBuilder#actionRow instead.")
public inline fun MessageBuilder.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val actionRow = ActionRowBuilder().apply(builder)
    components?.add(actionRow) ?: run { components = mutableListOf(actionRow) }
}

/**
 * Adds an [container][ContainerBuilder] configured by the [builder] to the [components][MessageBuilder.components] of
 * the message.
 *
 * A message can have up to ten top-level components.
 */
public inline fun MessageBuilder.container(builder: ContainerBuilder.() -> Unit) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val container = ContainerBuilder().apply(builder)
    components?.add(container) ?: run { components = mutableListOf(container) }
}

/**
 * Adds a [file][NamedFile] with [name] and [contentProvider] to [files][MessageBuilder.files].
 *
 * The corresponding attachment object can be configured with [builder].
 */
public inline fun MessageBuilder.addFile(
    name: String,
    contentProvider: ChannelProvider,
    builder: AttachmentBuilder.() -> Unit,
): NamedFile {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    // see https://discord.com/developers/docs/reference#uploading-files:
    // we use the index of a file in the `files` list as `n` in `files[n]`, as implemented in `MultipartRequest.data`
    /** (clickable link: [MultipartRequest.data]) */
    val file = NamedFile(name, contentProvider)
    files.add(file)
    val index = files.lastIndex
    val attachment = AttachmentBuilder(id = Snowflake(index.toLong())).apply(builder)
    attachments?.add(attachment) ?: run { attachments = mutableListOf(attachment) }
    return file
}

/** Sets the [flags][MessageBuilder.flags] for the message. */
public inline fun MessageBuilder.messageFlags(builder: MessageFlags.Builder.() -> Unit) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    flags = MessageFlags(builder)
}


internal fun buildMessageFlags(
    base: MessageFlags?,
    suppressEmbeds: Boolean?,
    suppressNotifications: Boolean? = null,
    ephemeral: Boolean? = null,
): Optional<MessageFlags> =
    if (base == null && suppressEmbeds == null && suppressNotifications == null && ephemeral == null) {
        Optional.Missing()
    } else {
        val flags = MessageFlags {
            if (base != null) +base
            fun apply(add: Boolean?, flag: MessageFlag) = when (add) {
                true -> +flag
                false -> -flag
                null -> {}
            }
            apply(suppressEmbeds, MessageFlag.SuppressEmbeds)
            apply(suppressNotifications, MessageFlag.SuppressNotifications)
            apply(ephemeral, MessageFlag.Ephemeral)
        }
        Optional.Value(flags)
    }
