package dev.kord.rest.builder

import dev.kord.common.annotation.KordDsl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

@KordDsl
interface RequestBuilder<T> {
    fun toRequest(): T
}

@KordDsl
interface AuditRequestBuilder<T> : RequestBuilder<T> {
    /**
     * The reason for this request, this will be displayed in the audit log.
     */
    var reason: String?
}

interface MultipleAttachmentsRequest {
    val files: MutableList<Pair<String, InputStream>>

    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }
}