package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import dev.kord.rest.builder.message.actionRow as actionRowExtensionOnNewSupertype
import dev.kord.rest.builder.message.allowedMentions as allowedMentionsExtensionOnNewSupertype
import dev.kord.rest.builder.message.embed as embedExtensionOnNewSupertype
import dev.kord.rest.builder.message.messageFlags as messageFlagsExtensionOnNewSupertype

@KordDsl
public sealed interface MessageModifyBuilder : MessageBuilder {

    @set:Deprecated(
        "This setter will be removed in the future, replace with files.clear() followed by files.addAll(...).",
        ReplaceWith("this.files.clear()\nthis.files.addAll(value)"),
        DeprecationLevel.HIDDEN,
    )
    override var files: MutableList<NamedFile>
}

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

    @set:Deprecated(
        "This setter will be removed in the future, replace with files.clear() followed by files.addAll(...).",
        ReplaceWith("this.files.clear()\nthis.files.addAll(value)"),
        DeprecationLevel.HIDDEN,
    )
    final override var files: MutableList<NamedFile> = mutableListOf()

    internal var _attachments: Optional<MutableList<AttachmentBuilder>?> = Optional.Missing()
    final override var attachments: MutableList<AttachmentBuilder>? by ::_attachments.delegate()
}


@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "RemoveRedundantQualifierName")
@kotlin.internal.LowPriorityInOverloadResolution
@Deprecated(
    "Replaced by extension on 'MessageBuilder'. Change import to 'dev.kord.rest.builder.message.embed'.",
    ReplaceWith("this.embed(block)", imports = ["dev.kord.rest.builder.message.embed"]),
    DeprecationLevel.HIDDEN,
)
public inline fun MessageModifyBuilder.embed(block: EmbedBuilder.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    embedExtensionOnNewSupertype(block)
}

/**
 * Configures the mentions that should trigger a ping. Not calling this function will result in the default behavior
 * (ping everything), calling this function but not configuring it before the request is build will result in all
 * pings being ignored.
 */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "RemoveRedundantQualifierName")
@kotlin.internal.LowPriorityInOverloadResolution
@Deprecated(
    "Replaced by extension on 'MessageBuilder'. Change import to 'dev.kord.rest.builder.message.allowedMentions'.",
    ReplaceWith("this.allowedMentions(block)", imports = ["dev.kord.rest.builder.message.allowedMentions"]),
    DeprecationLevel.HIDDEN,
)
public inline fun MessageModifyBuilder.allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    allowedMentionsExtensionOnNewSupertype(block)
}

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "RemoveRedundantQualifierName")
@kotlin.internal.LowPriorityInOverloadResolution
@Deprecated(
    "Replaced by extension on 'MessageBuilder'. Change import to 'dev.kord.rest.builder.message.actionRow'.",
    ReplaceWith("this.actionRow(builder)", imports = ["dev.kord.rest.builder.message.actionRow"]),
    DeprecationLevel.HIDDEN,
)
public inline fun MessageModifyBuilder.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    actionRowExtensionOnNewSupertype(builder)
}

/**
 * Sets/Unsets the [MessageFlags] for this message.
 *
 * **Only supports [MessageFlag.SuppressEmbeds]**
 */
@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "RemoveRedundantQualifierName")
@kotlin.internal.LowPriorityInOverloadResolution
@Deprecated(
    "Replaced by extension on 'MessageBuilder'. Change import to 'dev.kord.rest.builder.message.messageFlags'.",
    ReplaceWith("this.messageFlags(builder)", imports = ["dev.kord.rest.builder.message.messageFlags"]),
    DeprecationLevel.HIDDEN,
)
public inline fun MessageModifyBuilder.messageFlags(builder: MessageFlags.Builder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    messageFlagsExtensionOnNewSupertype(builder)
}
