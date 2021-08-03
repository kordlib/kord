package dev.kord.rest.builder.message.create

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * Message builder for a message that persists between client reloads.
 */
interface PersistentMessageCreateBuilder : MessageCreateBuilder {

    /**
     * The files to include as attachments.
     */
    val files: MutableList<Pair<String, InputStream>>

    /**
     * Adds a file with the [name] and [content] to the attachments.
     */
    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    /**
     * Adds a file with the given [path] to the attachments.
     */
    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

}
