package dev.kord.rest.builder.message.modify

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordAttachment
import dev.kord.rest.NamedFile
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.AllowedMentionsBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

sealed interface MessageModifyBuilder {

    var content: String?

    var embeds: MutableList<EmbedBuilder>?

    var allowedMentions: AllowedMentionsBuilder?

    @OptIn(KordPreview::class)
    var components: MutableList<MessageComponentBuilder>?


    /**
     * The files to include as attachments
     */
    var files: MutableList<NamedFile>?

    var attachments: MutableList<DiscordAttachment>?

    fun addFile(name: String, content: InputStream): NamedFile {
        val namedFile = NamedFile(name, content)

        files = (files ?: mutableListOf()).also {
            it.add(namedFile)
        }

        return namedFile
    }

    suspend fun addFile(path: Path): NamedFile = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

}

inline fun MessageModifyBuilder.embed(block: EmbedBuilder.() -> Unit) {
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
inline fun MessageModifyBuilder.allowedMentions(block: AllowedMentionsBuilder.() -> Unit = {}) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    allowedMentions = (allowedMentions ?: AllowedMentionsBuilder()).apply(block)
}


inline fun MessageModifyBuilder.actionRow(builder: ActionRowBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    components = (components ?: mutableListOf()).also {
        it.add(ActionRowBuilder().apply(builder))
    }
}
