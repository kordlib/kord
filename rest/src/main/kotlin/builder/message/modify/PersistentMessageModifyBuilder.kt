package dev.kord.rest.builder.message.modify

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
    var files: MutableList<Pair<String, InputStream>>?

    fun addFile(name: String, content: InputStream) {
        files = (files ?: mutableListOf()).also {
            it.add(name to content)
        }
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

}
