package dev.kord.rest.builder.message.modify

import dev.kord.common.entity.DiscordAttachment
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import java.io.InputStream
import java.nio.file.Path
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
     * Adds a file with the [name] and [content] to the attachments.
     */
    @Deprecated(
        "Use ByteReadChannel instead of InputStream",
        level = DeprecationLevel.WARNING
    )
    public fun addFile(name: String, content: InputStream): NamedFile = addFile(name, content.toByteReadChannel())

    /**
     * Adds a file with the [name] and [content] to the attachments.
     */
    public fun addFile(name: String, content: ByteReadChannel): NamedFile {
        val namedFile = NamedFile(name, content)

        files = (files ?: mutableListOf()).also {
            it.add(namedFile)
        }

        return namedFile
    }

    public suspend fun addFile(path: Path): NamedFile =
        addFile(path.fileName.toString(), path.readChannel())

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
