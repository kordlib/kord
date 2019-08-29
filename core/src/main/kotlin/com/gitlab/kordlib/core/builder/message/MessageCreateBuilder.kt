package com.gitlab.kordlib.core.builder.message

import com.gitlab.kordlib.core.builder.RequestBuilder
import com.gitlab.kordlib.rest.json.request.MessageCreateRequest
import com.gitlab.kordlib.rest.json.request.MultipartMessageCreateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class MessageCreateBuilder : RequestBuilder<MultipartMessageCreateRequest> {
    var content: String? = null
    var nonce: String? = null
    var tts: Boolean? = null
    var embed: EmbedBuilder? = null
    private val files: MutableList<Pair<String, InputStream>> = mutableListOf()

    inline fun embed(block: EmbedBuilder.() -> Unit) {
        embed = (embed ?: EmbedBuilder()).apply(block)
    }

    fun addFile(name: String, content: InputStream) {
        files + name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    override fun toRequest(): MultipartMessageCreateRequest = MultipartMessageCreateRequest(
            MessageCreateRequest(content, nonce, tts, embed?.toRequest()),
            files
    )

}