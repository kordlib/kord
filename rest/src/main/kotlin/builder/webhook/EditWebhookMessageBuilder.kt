package dev.kord.rest.builder.webhook

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.AllowedMentions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.component.MessageComponentBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.json.request.MultipartWebhookEditMessageRequest
import dev.kord.rest.json.request.WebhookEditMessageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class EditWebhookMessageBuilder : RequestBuilder<MultipartWebhookEditMessageRequest> {

    private var _content: Optional<String> = Optional.Missing()
    var content: String? by ::_content.delegate()

    var embeds: MutableList<EmbedBuilder> = mutableListOf()

    val files: MutableList<Pair<String, InputStream>> = mutableListOf()

    private var _allowedMentions: Optional<AllowedMentions> = Optional.Missing()
    var allowedMentions: AllowedMentions? by ::_allowedMentions.delegate()

    val components: MutableList<MessageComponentBuilder> = mutableListOf()

    @OptIn(ExperimentalContracts::class)
    inline fun embed(builder: EmbedBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        embeds.add(EmbedBuilder().apply(builder))
    }

    fun addFile(name: String, content: InputStream) {
        files += name to content
    }

    suspend fun addFile(path: Path) = withContext(Dispatchers.IO) {
        addFile(path.fileName.toString(), Files.newInputStream(path))
    }

    @OptIn(ExperimentalContracts::class)
    @KordPreview
    inline fun <T> actionRow(builder: ActionRowBuilder.() -> Unit) {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }

        components.add(ActionRowBuilder().apply(builder))
    }

    override fun toRequest(): MultipartWebhookEditMessageRequest = MultipartWebhookEditMessageRequest(
        WebhookEditMessageRequest(
            _content,
            Optional.missingOnEmpty(embeds.map(EmbedBuilder::toRequest)),
            _allowedMentions,
            Optional.missingOnEmpty(components.map { it.build() })
        ),
        files
    )
}
