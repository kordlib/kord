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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AbstractMessageCreateBuilder

        if (content != other.content) return false
        if (tts != other.tts) return false
        if (embeds != other.embeds) return false
        if (allowedMentions != other.allowedMentions) return false
        if (components != other.components) return false
        if (files != other.files) return false
        if (attachments != other.attachments) return false
        if (flags != other.flags) return false
        if (suppressEmbeds != other.suppressEmbeds) return false
        if (suppressNotifications != other.suppressNotifications) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + (tts?.hashCode() ?: 0)
        result = 31 * result + (embeds?.hashCode() ?: 0)
        result = 31 * result + (allowedMentions?.hashCode() ?: 0)
        result = 31 * result + (components?.hashCode() ?: 0)
        result = 31 * result + files.hashCode()
        result = 31 * result + (attachments?.hashCode() ?: 0)
        result = 31 * result + (flags?.hashCode() ?: 0)
        result = 31 * result + (suppressEmbeds?.hashCode() ?: 0)
        result = 31 * result + (suppressNotifications?.hashCode() ?: 0)
        return result
    }

}
