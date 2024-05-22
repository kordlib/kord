package dev.kord.rest.builder.message.create

import dev.kord.common.annotation.KordDsl
import dev.kord.common.annotation.KordUnsafe
import dev.kord.rest.json.request.CreatablePoll
import dev.kord.common.entity.DiscordPoll
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.AttachmentBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.MessageBuilder
import dev.kord.rest.builder.message.PollBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import dev.kord.rest.builder.message.actionRow as actionRowExtensionOnNewSupertype
import dev.kord.rest.builder.message.allowedMentions as allowedMentionsExtensionOnNewSupertype
import dev.kord.rest.builder.message.embed as embedExtensionOnNewSupertype
import dev.kord.rest.builder.message.messageFlags as messageFlagsExtensionOnNewSupertype

/**
 * The base builder for creating a new message.
 */
@KordDsl
public sealed interface MessageCreateBuilder : MessageBuilder {

    /**
     * The poll of this message.
     */
    @set:KordUnsafe
    public var poll: CreatablePoll?

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

    override val files: MutableList<NamedFile> = mutableListOf()

    internal var _attachments: Optional<MutableList<AttachmentBuilder>> = Optional.Missing()
    final override var attachments: MutableList<AttachmentBuilder>? by ::_attachments.delegate()

    final override var flags: MessageFlags? = null
    final override var suppressEmbeds: Boolean? = null
    final override var suppressNotifications: Boolean? = null

    internal var _poll: Optional<CreatablePoll> = Optional.Missing()
    @KordUnsafe
    final override var poll: CreatablePoll? by ::_poll.delegate()
}


/**
 * Adds an embed to the message, configured by the [block]. A message can have up to 10 embeds.
 */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
@Deprecated(
    "Replaced by extension on 'MessageBuilder'. Change import to 'dev.kord.rest.builder.message.embed'.",
    ReplaceWith("this.embed(block)", imports = ["dev.kord.rest.builder.message.embed"]),
    DeprecationLevel.HIDDEN,
)
public inline fun MessageCreateBuilder.embed(block: EmbedBuilder.() -> Unit) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    embedExtensionOnNewSupertype(block)
}

/**
 * Configures the mentions that should trigger a mention (aka ping). Not calling this function will result in the default behavior
 * (ping everything), calling this function but not configuring it before the request is build will result in all
 * pings being ignored.
 */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
@Deprecated(
    "Replaced by extension on 'MessageBuilder'. Change import to 'dev.kord.rest.builder.message.allowedMentions'.",
    ReplaceWith("this.allowedMentions(block)", imports = ["dev.kord.rest.builder.message.allowedMentions"]),
    DeprecationLevel.HIDDEN,
)
public inline fun MessageCreateBuilder.allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    allowedMentionsExtensionOnNewSupertype(block)
}

/**
 * Adds an Action Row to the message, configured by the [builder]. A message can have up to 5 action rows.
 */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
@Deprecated(
    "Replaced by extension on 'MessageBuilder'. Change import to 'dev.kord.rest.builder.message.actionRow'.",
    ReplaceWith("this.actionRow(builder)", imports = ["dev.kord.rest.builder.message.actionRow"]),
    DeprecationLevel.HIDDEN,
)
public inline fun MessageCreateBuilder.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    actionRowExtensionOnNewSupertype(builder)
}

/**
 * Sets the [MessageFlags] for the created message.
 *
 * **Only supports [MessageFlag.SuppressEmbeds] and [MessageFlag.SuppressNotifications]**
 */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@kotlin.internal.LowPriorityInOverloadResolution
@Deprecated(
    "Replaced by extension on 'MessageBuilder'. Change import to 'dev.kord.rest.builder.message.messageFlags'.",
    ReplaceWith("this.messageFlags(builder)", imports = ["dev.kord.rest.builder.message.messageFlags"]),
    DeprecationLevel.HIDDEN,
)
public inline fun MessageCreateBuilder.messageFlags(builder: MessageFlags.Builder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    messageFlagsExtensionOnNewSupertype(builder)
}

/**
 * Set's the [poll][dev.kord.common.entity.DiscordMessage.poll] of this message.
 *
 * **Please note that if poll is set, you currently cannot set [MessageBuilder.content],
 * [MessageBuilder.attachments], [MessageBuilder.embeds] or [MessageBuilder.components]**
 */
@KordUnsafe
public inline fun MessageCreateBuilder.poll(builder: PollBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }

    poll = PollBuilder().apply(builder).toRequest()
}
