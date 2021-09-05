package dev.kord.rest.builder.message.modify

import dev.kord.common.entity.DiscordAttachment
import dev.kord.rest.NamedFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * Message builder for a message that persists between client reloads.
 */
interface PersistentMessageModifyBuilder : MessageModifyBuilder {

    /**
     * The files to include as attachments
     */
    var files: MutableList<NamedFile>?

    var attachments: MutableList<DiscordAttachment>?

    fun addFile(name: String, content: InputStream) {
        files = (files ?: mutableListOf()).also {
            it.add(NamedFile(name, content))
        }
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

}
