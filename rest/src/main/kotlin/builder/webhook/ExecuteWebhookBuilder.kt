package dev.kord.rest.builder.webhook

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.EmbedRequest
import dev.kord.rest.json.request.MultiPartWebhookExecuteRequest
import dev.kord.rest.json.request.WebhookExecuteRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordDsl
class ExecuteWebhookBuilder : RequestBuilder<MultiPartWebhookExecuteRequest> {

    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    private var _username: Optional<String> = Optional.Missing()
    var username: String? by ::_username.delegate()

    private var _avatarUrl: Optional<String> = Optional.Missing()
    var avatarUrl: String? by ::_avatarUrl.delegate()

    private var _tts: OptionalBoolean = OptionalBoolean.Missing
    var tts: Boolean? by ::_tts.delegate()

    private var file: Pair<String, java.io.InputStream>? = null
    var embeds: MutableList<EmbedRequest> = mutableListOf()

    fun setFile(name: String, content: java.io.InputStream) {
        file = name to content
    }

    suspend fun setFile(path: Path) = withContext(Dispatchers.IO) {
        setFile(path.fileName.toString(), Files.newInputStream(path))
    }

    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        embeds.add(EmbedBuilder().apply(builder).toRequest())
    }

    override fun toRequest(): MultiPartWebhookExecuteRequest = MultiPartWebhookExecuteRequest(
        WebhookExecuteRequest(_content, _username, _avatarUrl, _tts, Optional.missingOnEmpty(embeds)), file
    )

}
