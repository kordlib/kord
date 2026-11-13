package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.*
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

@KordDsl
public sealed interface MessageModifyBuilder : MessageBuilder

/**
 * Keeps the attachment with the given [id], so it will be present after editing the message.
 *
 * The attachment object can optionally be edited with [builder].
 */
public inline fun MessageModifyBuilder.keepAttachment(id: Snowflake, builder: AttachmentBuilder.() -> Unit = {}) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val attachment = AttachmentBuilder(id).apply(builder)
    attachments?.add(attachment) ?: run { attachments = mutableListOf(attachment) }
}


// this could have been combined with MessageModifyBuilder into a single sealed class, but it would have broken binary
// compatibility, because MessageModifyBuilder would have changed from interface to class
@Suppress("PropertyName")
@KordDsl
public sealed class AbstractMessageModifyBuilder : MessageModifyBuilder {

    internal var _content: Optional<String?> = Optional.Missing()
    final override var content: String? by ::_content.delegate()

    internal var _embeds: Optional<MutableList<EmbedBuilder>?> = Optional.Missing()
    final override var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    private var _flags: Optional<MessageFlags?> = Optional.Missing()
    final override var flags: MessageFlags? by ::_flags.delegate()
    final override var suppressEmbeds: Boolean? = null
    internal fun buildFlags(): Optional<MessageFlags?> =
        suppressEmbeds?.let { buildMessageFlags(flags, suppressEmbeds = it) } ?: _flags

    internal var _allowedMentions: Optional<AllowedMentionsBuilder?> = Optional.Missing()
    final override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    internal var _components: Optional<MutableList<MessageComponentBuilder>?> = Optional.Missing()
    final override var components: MutableList<MessageComponentBuilder>? by ::_components.delegate()

    final override val files: MutableList<NamedFile> = mutableListOf()

    internal var _attachments: Optional<MutableList<AttachmentBuilder>?> = Optional.Missing()
    final override var attachments: MutableList<AttachmentBuilder>? by ::_attachments.delegate()
}
