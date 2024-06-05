package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.AttachmentBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.MessageBuilder

/**
 * The base builder for creating a new message.
 */
@KordDsl
public sealed interface MessageCreateBuilder : MessageBuilder {

    /** Whether this message should be played as a text-to-speech message. */
    public var tts: Boolean?

    /**
     * Optional custom [MessageFlags] to add to the message created.
     *
     * @see suppressEmbeds
     * @see suppressNotifications
     */
    override var flags: MessageFlags?

    /** This message will not trigger push and desktop notifications. */
    public var suppressNotifications: Boolean?
}


// this could have been combined with MessageCreateBuilder into a single sealed class, but it would have broken binary
// compatibility, because MessageCreateBuilder would have changed from interface to class
@Suppress("PropertyName")
@KordDsl
public sealed class AbstractMessageCreateBuilder : MessageCreateBuilder {

    internal var _content: Optional<String> = Optional.Missing()
    final override var content: String? by ::_content.delegate()

    internal var _tts: OptionalBoolean = OptionalBoolean.Missing
    final override var tts: Boolean? by ::_tts.delegate()

    internal var _embeds: Optional<MutableList<EmbedBuilder>> = Optional.Missing()
    final override var embeds: MutableList<EmbedBuilder>? by ::_embeds.delegate()

    internal var _allowedMentions: Optional<AllowedMentionsBuilder> = Optional.Missing()
    final override var allowedMentions: AllowedMentionsBuilder? by ::_allowedMentions.delegate()

    internal var _components: Optional<MutableList<MessageComponentBuilder>> = Optional.Missing()
    final override var components: MutableList<MessageComponentBuilder>? by ::_components.delegate()

    final override val files: MutableList<NamedFile> = mutableListOf()

    internal var _attachments: Optional<MutableList<AttachmentBuilder>> = Optional.Missing()
    final override var attachments: MutableList<AttachmentBuilder>? by ::_attachments.delegate()

    final override var flags: MessageFlags? = null
    final override var suppressEmbeds: Boolean? = null
    final override var suppressNotifications: Boolean? = null
}
